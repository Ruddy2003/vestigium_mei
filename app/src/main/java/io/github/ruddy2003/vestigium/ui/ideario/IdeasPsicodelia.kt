package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Cara
import io.github.ruddy2003.vestigium.ui.EstadoTema
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.TrazosCiudad
import io.github.ruddy2003.vestigium.ui.TrazosPsicodelia
import io.github.ruddy2003.vestigium.ui.TrazosTinta
import io.github.ruddy2003.vestigium.ui.TrazosTinta.bandas
import io.github.ruddy2003.vestigium.ui.TrazosTinta.conRegistro
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Psicodelia (versión 2: dos caras)
// =====================================================================

/**
 * Psicodelia: la cajonera de planos del dibujante, en el cuarto de David (dibujante de carteles, San Francisco, hacia 1967),
 * contra una pared sin ventana (`docs/diseno/temas/psicodelia.md`, «Los muebles»). La pared es el yeso pintado de un
 * departamento victoriano viejo: verde salvia gastado con su moldura alta y el friso crema por encima, desconchones,
 * agujeros de clavo, una grieta, el cartel propio de David clavado con chinchetas (marco de arco, el disco con sus rayos que
 * ondulan, las bandas y unas letras de bloque inventadas, ilegibles) y un pase de autobús prendido con una chincheta. La
 * cajonera es de roble, de dos cuerpos de cajones anchos y bajos bajo una tapa gruesa, con su zócalo y el portatarjetas de
 * latón del larguero de abajo (la placa de «y N más»). Encima, el punto focal a la izquierda: la mesa de dibujo inclinada
 * de sobremesa con el cartel a medias (la mitad entintada, la otra mitad todavía a lápiz) pegado con cinta de pintor, la
 * regla T cruzada encima y la lámpara de brazo con resortes, prensada al tablero, que cruza la moldura y se asoma sobre el
 * cartel. A la derecha, el retroproyector de las luces líquidas con sus dos vidrios de reloj de aceite teñido y el cable que
 * cuelga por el canto; delante, dos frascos de tinta china (uno destapado, con la plumilla atravesada y el tapón tumbado), el
 * rollo de cinta de pintor con la punta que cae por el canto, y atrás la lata de café con plumillas y pinceles. Al pie, sobre
 * el suelo de tabla, el tubo de carteles apoyado en el costado (cortado por el borde izquierdo), una bola de papel y la
 * papelera de lata llena de bocetos arrugados (cortada por el derecho).
 *
 * Cada tema es, según la semilla, un cajón de la planera con su tarjeta en el portatarjetas de latón y los pliegos que
 * asoman por arriba (más con más fichas; uno sobresale con un trozo de cartel impreso); el cajón entreabierto, con sus
 * costados a la vista, los pliegos dentro y una regla o una escuadra que asoma; o, metida en el hueco, una caja plana de
 * papel fotográfico o un portafolios de cartón atado con cinta. Cada clase cambia de madera, de cartón o de cinta según la
 * semilla. Las ideas sin tema van en la caja de cartón de los recortes, abierta, con los recortes que asoman; un hueco libre
 * es un cajón cerrado con el portatarjetas vacío. Al abrir uno, el cajón sale hacia quien mira.
 *
 * Dos caras, un solo dibujo (la forma y el sitio de cada cosa son los mismos; cambia el pintor, ver [Mat] y [c]):
 * - **Ciudad · claro:** la luz de la tarde entra de arriba a la izquierda desde fuera del cuadro; sombras café, grano de
 *   película.
 * - **Ciudad · oscuro:** alumbra la lámpara de brazo: halo en la pared, el cartel del tablero encendido, filo cálido en los
 *   cantos que la miran; los cajones de arriba a la izquierda encendidos y los de abajo casi perdidos.
 * - **Psicodelia · claro:** el mueble impreso a tintas planas (ultramar, magenta y, con la intensidad, bermellón) sobre el
 *   papel, con la línea de la tinta de grabar y trama de líneas en el lado de sombra.
 * - **Psicodelia · oscuro:** la luz es la proyección de las luces líquidas en la pared (tres manchas planas de tinta,
 *   magenta, ultramar y bermellón, que el retroproyector, ahora encendido, echa sobre el yeso): encienden los cantos de lo que
 *   tienen delante. Con la intensidad ([EstadoTema.intensidad]), las manchas crecen y suben de croma, la trama ondula, el
 *   registro de los carteles se corre y la de bermellón aparece desde i = 0.5.
 * Por dentro (sin cambios), la caja de sencillos vista desde arriba; las fichas son fundas de papel de sencillo.
 */
object IdeasPsicodelia : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta de la ciudad (sale de `TrazosCiudad`)
    /** El yeso de la pared, verde salvia gastado; el friso crema de encima de la moldura y la moldura pintada. */
    private val PARED = Color(0xFFA2A68C)
    private val FRISO = Color(0xFFD9CDB6)
    private val MOLDURA = Color(0xFFE3D7BE)
    /** El roble de la planera, el nogal de algunos frentes y el acero pintado de verde gris de otros; el fondo de un hueco. */
    private val ROBLE = Color(0xFFA06D3C)
    private val NOGAL = Color(0xFF6E4630)
    private val ACERO = Color(0xFF7D8A79)
    private val HUECO = Color(0xFF3A2A20)
    /** El tablero de abedul de la mesa de dibujo. */
    private val TABLERO = Color(0xFFCBB089)
    /** Los papeles: el de cartel, el marfil, el cartón de estraza y el gris. */
    private val PAPEL = Color(0xFFEDE3CD)
    private val MARFIL = Color(0xFFDDCFB0)
    private val KRAFT = Color(0xFFB08A5A)
    private val GRIS = Color(0xFF8F887B)
    /** Los metales: el latón, el esmalte verde botella de la lámpara, la chapa del retroproyector y el cromo. */
    private val LATON = Color(0xFFB8914A)
    private val ESMALTE = Color(0xFF3D5838)
    private val CHAPA = Color(0xFFA59C88)
    private val CROMO = Color(0xFFC4BFB2)
    /** El vidrio de los frascos y la tinta china. */
    private val VIDRIO = Color(0xFF8FA39A)
    private val TINTA = Color(0xFF2A2119)
    /** Los colores de época, apagados por la película: el ladrillo, el naranja quemado, el verde, el azul y el rosa. */
    private val ROJO = TrazosCiudad.COCHE
    private val NARANJA = TrazosCiudad.ETIQUETA
    private val VERDE = Color(0xFF56704C)
    private val AZUL = Color(0xFF4C5A86)
    private val ROSA = Color(0xFF9A5470)
    /** La cinta de pintor y el suelo de tabla. */
    private val CINTA = Color(0xFFD9C893)
    private val SUELO = Color(0xFF7A5232)
    /** La noche del cuarto, la pantalla encendida y su luz. */
    private val NOCHE = TrazosCiudad.PARED_NOCHE
    private val LAMPARA = TrazosCiudad.LAMPARA
    private val LAMPARA_LUZ = TrazosCiudad.LAMPARA_LUZ
    /** La tarjeta lejos de la luz, de noche: con la tinta del rótulo da más de 5:1. */
    private val PAPEL_LEJOS_CIUDAD = Color(0xFFA0978A)
    private val PAPEL_LEJOS_TINTA = Color(0xFFA69FA0)
    /** El grafito del lápiz del cartel a medias. */
    private val GRAFITO = Color(0xFF6B6458)

    /** Los materiales: cada uno tiene su color de ciudad y su tinta de cartel (ver [c]). */
    private enum class Mat { PARED, FRISO, MOLDURA, ROBLE, NOGAL, ACERO, HUECO, TABLERO, PAPEL, MARFIL, KRAFT, GRIS, LATON, ESMALTE, CHAPA, CROMO, VIDRIO, TINTA, ROJO, NARANJA, VERDE, AZUL, ROSA, CINTA, SUELO }

    private fun ciudad(m: Mat): Color = when (m) {
        Mat.PARED -> PARED
        Mat.FRISO -> FRISO
        Mat.MOLDURA -> MOLDURA
        Mat.ROBLE -> ROBLE
        Mat.NOGAL -> NOGAL
        Mat.ACERO -> ACERO
        Mat.HUECO -> HUECO
        Mat.TABLERO -> TABLERO
        Mat.PAPEL -> PAPEL
        Mat.MARFIL -> MARFIL
        Mat.KRAFT -> KRAFT
        Mat.GRIS -> GRIS
        Mat.LATON -> LATON
        Mat.ESMALTE -> ESMALTE
        Mat.CHAPA -> CHAPA
        Mat.CROMO -> CROMO
        Mat.VIDRIO -> VIDRIO
        Mat.TINTA -> TINTA
        Mat.ROJO -> ROJO
        Mat.NARANJA -> NARANJA
        Mat.VERDE -> VERDE
        Mat.AZUL -> AZUL
        Mat.ROSA -> ROSA
        Mat.CINTA -> CINTA
        Mat.SUELO -> SUELO
    }

    /** Qué tinta imprime cada material en el cartel: 0 papel, 1 grabado, 2 ultramar, 3 magenta, 4 cálida (magenta que pasa a bermellón), 5 violeta de sobreimpresión. */
    private fun tintaDe(m: Mat): Int = when (m) {
        Mat.PARED -> 0
        Mat.FRISO -> 0
        Mat.MOLDURA -> 0
        Mat.ROBLE -> 2
        Mat.NOGAL -> 5
        Mat.ACERO -> 3
        Mat.HUECO -> 1
        Mat.TABLERO -> 4
        Mat.PAPEL -> 0
        Mat.MARFIL -> 0
        Mat.KRAFT -> 4
        Mat.GRIS -> 2
        Mat.LATON -> 4
        Mat.ESMALTE -> 1
        Mat.CHAPA -> 0
        Mat.CROMO -> 0
        Mat.VIDRIO -> 0
        Mat.TINTA -> 1
        Mat.ROJO -> 4
        Mat.NARANJA -> 4
        Mat.VERDE -> 2
        Mat.AZUL -> 2
        Mat.ROSA -> 3
        Mat.CINTA -> 0
        Mat.SUELO -> 5
    }

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.62f
    override val corona = 0.8f
    override val base = 0.3f
    override val lado = 0.07f
    override val hueco = 0.026f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** En la corona, en fracción de su alto: el fondo de la tapa, su canto de frente y su borde de abajo. */
    private const val SUP = 0.835f
    private const val FRENTE = 0.895f
    private const val CANTO = 0.955f
    /** En la base, en fracción de su alto: el rincón de la pared con el suelo, el pie del larguero y donde pisa el zócalo. */
    private const val RINCON = 0.34f
    private const val LARGUERO = 0.38f
    private const val PISA = 0.49f
    /** Los costados de la cajonera, en fracción del ancho. */
    private const val IZQ = 0.03f
    private const val DER = 0.97f
    /** Cuánto sale el cajón al abrirlo, en fracción del alto del hueco. */
    private const val SALIDA = 0.06f

    // ---------------------------------------------------------------- el pintor: cara, noche, intensidad y luz

    /** Lo que sabe el pintor del dibujo que se está pintando (se pone al empezar [mueble] y [compartimento]). */
    private var cara = Cara.BASE
    private var o = false
    private var ii = 0f
    private var med = TrazosTinta.medidas(0f)
    /** El ancho del mueble entero y dónde empieza, dentro de él, el lienzo que se pinta (en píxeles). */
    private var anchoW = 1f
    private var ox = 0f
    private var oy = 0f
    /** Si es mayor o igual que 0, la luz de todo el lienzo (una muestra suelta, fuera del mueble). */
    private var kFijo = -1f

    private val tinta: Boolean get() = cara == Cara.INTENSA

    private fun empezar(oscuro: Boolean, w: Float, x0: Float, y0: Float, fijo: Float) {
        cara = EstadoTema.cara
        ii = if (cara == Cara.INTENSA) EstadoTema.intensidad.let { if (it.isNaN()) 0f else it.coerceIn(0f, 1f) } else 0f
        med = TrazosTinta.medidas(ii)
        o = oscuro
        anchoW = maxOf(w, 1f)
        ox = x0; oy = y0
        kFijo = fijo
    }

    /** La pantalla de la lámpara de brazo (de noche, en la ciudad, la luz), en fracciones del ancho. */
    private val LAMP = Offset(0.323f, 0.19f)
    /** Las tres manchas de luz líquida en la pared (de noche, en la psicodelia), en fracciones del ancho, y su radio. */
    private val MANCHA_M = Offset(0.3f, 0.13f)
    private val MANCHA_U = Offset(0.78f, 0.2f)
    private val MANCHA_B = Offset(0.56f, 0.12f)

    /** La caída de la luz por el frente de la cajonera: el alto (en fracciones del ancho) y cuánta llega ahí. */
    private val CAIDA_CIUDAD = listOf(0.8f to 0.95f, 1.08f to 0.8f, 1.36f to 0.56f, 1.64f to 0.32f, 1.92f to 0.15f, 2.2f to 0.07f, 2.7f to 0.035f)
    private val CAIDA_TINTA = listOf(0.8f to 0.92f, 1.08f to 0.72f, 1.36f to 0.42f, 1.64f to 0.18f, 1.92f to 0.06f, 2.2f to 0.03f, 2.7f to 0.02f)

    private fun caida(t: List<Pair<Float, Float>>, y: Float): Float {
        if (y < t.first().first) return 0f
        for (i in 0 until t.size - 1) {
            val (y0, k0) = t[i]; val (y1, k1) = t[i + 1]
            if (y in y0..y1) return k0 + (k1 - k0) * (y - y0) / (y1 - y0)
        }
        return t.last().second
    }

    private fun dist(a: Offset, b: Offset): Float { val dx = a.x - b.x; val dy = a.y - b.y; return sqrt(dx * dx + dy * dy) }

    /** Las tres manchas, con su peso: el bermellón entra con la intensidad; todas crecen con ella. */
    private fun manchas(g: Offset): FloatArray {
        val e = 2.9f / (1f + 0.35f * ii)
        return floatArrayOf(1.2f * exp(-e * dist(g, MANCHA_M)), 1.2f * exp(-e * dist(g, MANCHA_U)), 1.2f * med.bermellon * exp(-e * dist(g, MANCHA_B)))
    }

    /** Cuánta luz le llega a un punto [p] del lienzo (en píxeles), de noche: de la lámpara o de las manchas. De día, 0. */
    private fun k(p: Offset): Float {
        if (!o) return 0f
        if (kFijo >= 0f) return kFijo
        val g = Offset((ox + p.x) / anchoW, (oy + p.y) / anchoW)
        return if (!tinta) {
            val lado = (1f - 0.6f * (abs(g.x - LAMP.x) - 0.12f).coerceAtLeast(0f)).coerceIn(0.72f, 1f)
            maxOf(1.3f * exp(-3.2f * dist(g, LAMP)), caida(CAIDA_CIUDAD, g.y) * lado).coerceIn(0.03f, 1f)
        } else {
            val m = manchas(g)
            maxOf(maxOf(m[0], m[1], m[2]), caida(CAIDA_TINTA, g.y) * (0.85f + 0.25f * ii)).coerceIn(0.03f, 1f)
        }
    }

    /** De noche en la psicodelia, el color de la mancha que más alumbra un punto: el que enciende sus cantos. */
    private fun canto(p: Offset): Color {
        val g = Offset((ox + p.x) / anchoW, (oy + p.y) / anchoW)
        val m = manchas(g)
        val j = if (m[2] > m[0] && m[2] > m[1]) 2 else if (m[1] > m[0]) 1 else 0
        return when (j) {
            0 -> TrazosTinta.tinta(TrazosTinta.Tinta.MAGENTA, true, ii)
            1 -> TrazosTinta.tinta(TrazosTinta.Tinta.ULTRAMAR, true, ii)
            else -> TrazosTinta.tinta(TrazosTinta.Tinta.BERMELLON, true, ii)
        }
    }

    /** ¿La luz le llega a [p] por la izquierda? De día, siempre (la tarde); de noche, según dónde esté la luz. */
    private fun luzIzq(p: Offset): Boolean {
        if (!o) return true
        val gx = (ox + p.x) / anchoW
        if (!tinta) return gx > LAMP.x - 0.02f
        val gy = (oy + p.y) / anchoW
        val m = manchas(Offset(gx, gy))
        val cx = if (m[2] > m[0] && m[2] > m[1]) MANCHA_B.x else if (m[1] > m[0]) MANCHA_U.x else MANCHA_M.x
        return gx > cx
    }

    private val MAG get() = TrazosTinta.tinta(TrazosTinta.Tinta.MAGENTA, o, ii)
    private val ULT get() = TrazosTinta.tinta(TrazosTinta.Tinta.ULTRAMAR, o, ii)
    private val BER get() = TrazosTinta.tinta(TrazosTinta.Tinta.BERMELLON, o, ii)
    private val CALIDA get() = lerp(MAG, BER, med.bermellon)
    private val GRABAR get() = TrazosTinta.grabado(false)
    private val PAPEL_T get() = TrazosTinta.papel(false)
    private val CREMA get() = TrazosTinta.CREMA
    private val NEGRO get() = TrazosTinta.NEGRO

    /** La tinta de un material, sobre papel o sobre negro (sin luz). */
    private fun tintaColor(m: Mat): Color = when (tintaDe(m)) {
        0 -> if (o) CREMA else PAPEL_T
        1 -> if (o) lerp(NEGRO, ULT, 0.3f) else GRABAR
        2 -> ULT
        3 -> MAG
        4 -> CALIDA
        else -> TrazosTinta.sobreimpresion(MAG, ULT, o)
    }

    /** El color propio de algo, hundido en la noche del cuarto (de su mismo tono, no gris). */
    private fun hondo(c: Color) = Color(c.red * 0.14f + NOCHE.red * 0.78f, c.green * 0.14f + NOCHE.green * 0.78f, c.blue * 0.14f + NOCHE.blue * 0.78f, c.alpha)

    /**
     * El color de un material [m] en el punto [p] del lienzo, según la cara y la noche. Ciudad clara: su color, un poco más
     * claro arriba (la tarde). Ciudad oscura: de su color hondo al suyo según la luz de la lámpara, teñido de ella. Cartel
     * claro: su tinta plana. Cartel oscuro: su tinta sobre el negro, más encendida cerca de las manchas de luz.
     */
    private fun c(m: Mat, p: Offset): Color {
        if (!tinta) {
            val base = ciudad(m)
            if (!o) {
                val gy = (oy + p.y) / anchoW
                return lerp(base, TrazosCiudad.AL_SOL, (0.09f * (1f - gy / 2.6f)).coerceIn(0f, 0.09f))
            }
            val k = k(p)
            return lerp(lerp(hondo(base), base, (1.05f * k).coerceAtMost(0.92f)), LAMPARA_LUZ, 0.2f * k)
        }
        val t = tintaColor(m)
        if (!o) return t
        val k = k(p)
        // La luz baja por filas: más honda que una recta, para que lo lejano se pierda de verdad.
        val kk = k.pow(1.3f)
        return if (tintaDe(m) == 0) lerp(lerp(NEGRO, CREMA, 0.16f), CREMA, (0.1f + 0.9f * kk).coerceAtMost(1f))
        else lerp(lerp(NEGRO, t, 0.12f), t, (0.05f + 0.9f * kk).coerceAtMost(0.95f))
    }

    /** La sombra con color: café de día en la ciudad, la noche del cuarto, la tinta de grabar o el negro de terciopelo. */
    private fun s(a: Float): Color {
        val x = a.coerceIn(0f, 1f)
        return when {
            !tinta && !o -> TrazosCiudad.SOMBRA.copy(alpha = x)
            !tinta -> NOCHE.copy(alpha = (x * 1.3f).coerceAtMost(1f))
            !o -> GRABAR.copy(alpha = (x * 0.85f).coerceAtMost(1f))
            else -> NEGRO.copy(alpha = (x * 1.4f).coerceAtMost(1f))
        }
    }

    /** La luz en lo que la mira: crema de día, la de la lámpara o la de la mancha más cercana de noche, el papel en el cartel. */
    private fun b(a: Float, p: Offset): Color {
        val x = a.coerceIn(0f, 1f)
        return when {
            !tinta && !o -> TrazosCiudad.AL_SOL.copy(alpha = x)
            !tinta -> LAMPARA.copy(alpha = (x * 1.1f * k(p)).coerceAtMost(1f))
            !o -> PAPEL_T.copy(alpha = (x * 1.6f).coerceAtMost(1f))
            else -> canto(p).copy(alpha = (x * 1.6f * k(p)).coerceAtMost(1f))
        }
    }

    /** La línea de dibujo del cartel: la tinta de grabar sobre papel; sobre negro, el crema apagado. Nada en la ciudad. */
    private fun DrawScope.linea(forma: Path, p: Offset, grosor: Float = 1f) {
        if (!tinta) return
        val u = density
        if (!o) drawPath(forma, GRABAR, style = Stroke(grosor * 1.05f * u, join = StrokeJoin.Round))
        else drawPath(forma, CREMA.copy(alpha = (0.16f + 0.4f * k(p)).coerceAtMost(0.6f)), style = Stroke(grosor * 0.8f * u, join = StrokeJoin.Round))
    }

    /**
     * Rayas paralelas sobre [r] (dentro del recorte que ya haya), giradas [angulo] grados: la trama de grabado del cartel. Con
     * la intensidad, ondulan. El paso nunca baja de 1 px.
     */
    private fun DrawScope.rayado(r: Rect, paso: Float, grosor: Float, color: Color, angulo: Float = -45f, onda: Float = ii * 1.7f) {
        if (r.width <= 0f || r.height <= 0f || !r.width.isFinite() || !r.height.isFinite()) return
        val u = density
        val p = maxOf(paso, 1f)
        val rad = sqrt(r.width * r.width + r.height * r.height) / 2f + p
        val cc = r.center
        val amp = onda * u
        clipRect(r.left, r.top, r.right, r.bottom) {
            rotate(angulo, cc) {
                var y = cc.y - rad
                var k = 0
                while (y <= cc.y + rad) {
                    if (amp < 0.3f) drawLine(color, Offset(cc.x - rad, y), Offset(cc.x + rad, y), grosor)
                    else {
                        val q = Path()
                        val pasoX = maxOf(6f * u, 1f)
                        var x = cc.x - rad
                        q.moveTo(x, y)
                        while (x < cc.x + rad) {
                            x += pasoX
                            q.lineTo(x, y + amp * sin(x / (11f * u) + k * 0.7f))
                        }
                        drawPath(q, color, style = Stroke(grosor))
                    }
                    y += p; k++
                }
            }
        }
    }

    /** Las formas de un volumen: de frente (sombra a un lado), redondo de pie (bandas) o tumbado (luz arriba). */
    private const val PLANO = 0
    private const val REDONDO = 1
    private const val TUMBADO = 2

    /**
     * Una pieza con volumen: [forma] de [m], con su caja [r] (para su luz). En la ciudad, el lado de la luz más claro y el otro
     * en sombra café; de noche, además, el filo de la lámpara. En el cartel claro, la trama de grabado en el lado de sombra y
     * la línea de tinta; en el oscuro, el canto que mira a la mancha encendido de su color.
     */
    private fun DrawScope.pieza(forma: Path, m: Mat, r: Rect, tipo: Int = PLANO, fuerza: Float = 1f, color: Color? = null) {
        val u = density
        val cc = r.center
        drawPath(forma, color ?: c(m, cc))
        val izq = luzIzq(cc)
        clipPath(forma) {
            if (tinta && !o) {
                val sombra = when (tipo) {
                    TUMBADO -> Rect(r.left, r.top + r.height * 0.62f, r.right, r.bottom)
                    REDONDO -> if (izq) Rect(r.left + r.width * 0.62f, r.top, r.right, r.bottom) else Rect(r.left, r.top, r.left + r.width * 0.38f, r.bottom)
                    else -> if (izq) Rect(r.left + r.width * 0.8f, r.top, r.right, r.bottom) else Rect(r.left, r.top, r.left + r.width * 0.2f, r.bottom)
                }
                val sobre = tintaDe(m) == 1
                rayado(sombra, 2.6f * u, 0.85f * u, if (sobre) PAPEL_T.copy(alpha = 0.5f) else GRABAR)
                if (tipo == REDONDO) drawRect(PAPEL_T, Offset(if (izq) r.left + r.width * 0.16f else r.right - r.width * 0.23f, r.top), Size(r.width * 0.07f, r.height))
                if (tipo == TUMBADO) drawRect(PAPEL_T, Offset(r.left, r.top + r.height * 0.14f), Size(r.width, maxOf(r.height * 0.06f, 0.8f * u)))
            } else {
                fun x(f: Float, a: Float) = if (izq) r.left + r.width * f else r.right - r.width * (f + a)
                when (tipo) {
                    REDONDO -> {
                        drawRect(b(0.2f * fuerza, cc), Offset(x(0f, 0.3f), r.top), Size(r.width * 0.3f, r.height))
                        drawRect(b(0.32f * fuerza, cc), Offset(x(0.14f, 0.08f), r.top), Size(r.width * 0.08f, r.height))
                        drawRect(s(0.16f * fuerza), Offset(x(0.58f, 0.42f), r.top), Size(r.width * 0.42f, r.height))
                        drawRect(s(0.2f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
                    }
                    TUMBADO -> {
                        drawRect(b(0.2f * fuerza, cc), r.topLeft, Size(r.width, r.height * 0.3f))
                        drawRect(s(0.15f * fuerza), Offset(r.left, r.top + r.height * 0.6f), Size(r.width, r.height * 0.4f))
                        drawRect(s(0.18f * fuerza), Offset(r.left, r.top + r.height * 0.84f), Size(r.width, r.height * 0.16f))
                    }
                    else -> {
                        drawRect(b(0.12f * fuerza, cc), Offset(x(0f, 0.25f), r.top), Size(r.width * 0.25f, r.height))
                        drawRect(s(0.18f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
                        drawRect(b(0.3f * fuerza, cc), r.topLeft, Size(r.width, maxOf(1f * u, r.height * 0.04f)))
                    }
                }
            }
        }
        if (o) {
            val kk = k(cc)
            if (kk > 0.08f) {
                val col = if (tinta) canto(cc) else LAMPARA
                val lx = if (izq) r.left else r.right - r.width * 0.5f
                clipRect(lx, r.top, lx + r.width * 0.5f, r.bottom) {
                    drawPath(forma, col.copy(alpha = (0.85f * kk).coerceAtMost(if (tinta) 0.9f else 0.6f)), style = Stroke((if (tinta) 1.7f else 1.2f) * u))
                }
            }
        }
        linea(forma, cc)
    }

    private fun DrawScope.marco(r: Rect, col: Color, grosor: Float) = drawRect(col, r.topLeft, r.size, style = Stroke(grosor))

    private fun caja(r: Rect): Path = Path().apply { addRect(r) }
    private fun ovalo(r: Rect): Path = Path().apply { addOval(r) }
    private fun redondeado(r: Rect, radio: Float): Path = Path().apply { addRoundRect(RoundRect(r, CornerRadius(radio))) }

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i + 1 < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /** La sombra de contacto de lo que pisa la tapa o el suelo, corrida al lado contrario de la luz. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float = 0.3f) {
        val dx = if (luzIzq(centro)) -ancho * 0.36f else -ancho * 0.64f
        drawOval(s(a), Offset(centro.x + dx, centro.y - alto / 2), Size(ancho, alto))
    }

    /** La sombra de algo en la pared: de día, corrida a la derecha y abajo; de noche, lejos de la luz. */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, a: Float = 0.16f) {
        val w = anchoW
        val izq = luzIzq(desde)
        val dx = if (izq) w * 0.018f else -w * 0.018f
        if (o && k(desde) < 0.1f) return
        translate(dx, w * 0.012f) { drawPath(p, s(a)) }
    }

    // ---------------------------------------------------------------- el cartel (en la pared y en el tablero)

    /** Las tres tintas del cartel en esta cara: el magenta, el ultramar y el bermellón (apagados por la película en la ciudad). */
    private fun tintasCartel(p: Offset): Triple<Color, Color, Color> {
        if (tinta) return Triple(MAG, ULT, CALIDA)
        val m = TrazosCiudad.pelicula(Color(0xFFA94870)); val u = TrazosCiudad.pelicula(Color(0xFF3F4C9E)); val v = TrazosCiudad.pelicula(Color(0xFFC56851))
        if (!o) return Triple(m, u, v)
        val k = k(p)
        fun n(x: Color) = lerp(lerp(hondo(x), x, (1.05f * k).coerceAtMost(0.92f)), LAMPARA_LUZ, 0.15f * k)
        return Triple(n(m), n(u), n(v))
    }

    /**
     * El cartel de David en [r]: marco de arco, rayos que ondulan alrededor del disco con sus anillos, las bandas que fluyen
     * abajo y una fila de letras de bloque inventadas (ilegibles). Con [lapiz], solo el trazo de grafito (lo que falta entintar).
     */
    private fun DrawScope.disenoCartel(r: Rect, lapiz: Boolean, semilla: Int, estilo: Int = 0) {
        if (r.width <= 1f || r.height <= 1f) return
        val u = density
        val (tm, tu, tb) = tintasCartel(r.center)
        val papel = if (tinta && o) NEGRO else c(Mat.PAPEL, r.center)
        val grab = if (tinta) (if (o) CREMA else GRABAR) else c(Mat.TINTA, r.center)
        val trazo = if (tinta) (if (o) CREMA.copy(alpha = 0.45f) else GRABAR.copy(alpha = 0.5f)) else GRAFITO.copy(alpha = if (o) 0.4f else 0.6f)
        val marco = TrazosTinta.arco(Rect(r.left + r.width * 0.06f, r.top + r.height * 0.04f, r.right - r.width * 0.06f, r.bottom - r.height * 0.18f), r.width * 0.36f, 2f * u)
        val cd = Offset(r.center.x, r.top + r.height * 0.33f)
        val rd = r.width * 0.17f
        val rayos = TrazosTinta.rayos(cd, rd * 1.15f, r.width * 0.9f, 18, r.width * (0.03f + 0.05f * ii), 1.6f, semilla * 0.7f)
        val reg = med.registro
        if (lapiz) {
            drawPath(marco, trazo, style = Stroke(0.7f * u))
            clipPath(marco) { drawPath(rayos, trazo, style = Stroke(0.6f * u)) }
            drawCircle(trazo, rd, cd, style = Stroke(0.7f * u))
            drawCircle(trazo, rd * 0.35f, cd, style = Stroke(0.6f * u))
            letrasBloque(Rect(r.left + r.width * 0.08f, r.bottom - r.height * 0.15f, r.right - r.width * 0.08f, r.bottom - r.height * 0.03f), trazo, true, semilla)
            return
        }
        clipPath(marco) {
            drawRect(papel, r.topLeft, r.size)
            if (estilo == 1) {
                // Las bandas que fluyen y se abren alrededor del disco, en dos tintas.
                val nb = (med.bandasPorLado - 2).coerceIn(4, 10)
                conRegistro(TrazosTinta.Tinta.ULTRAMAR, reg) {
                    bandas(cd + Offset(0f, r.height * 0.12f), r.height * 1.1f, nb, r.width * 0.05f, r.width * (0.015f + 0.05f * ii), r.width * 0.02f, tu, 1.2f, semilla * 0.1f, 0.95f)
                }
                conRegistro(TrazosTinta.Tinta.MAGENTA, reg) {
                    bandas(cd + Offset(0f, r.height * 0.12f), r.height * 1.1f, nb - 1, r.width * 0.05f, r.width * (0.015f + 0.05f * ii), r.width * 0.008f, tm, 1.2f, semilla * 0.1f + 0.04f, 0.95f)
                }
            } else conRegistro(TrazosTinta.Tinta.ULTRAMAR, reg) { drawPath(rayos, tu) }
            // Las bandas que fluyen bajo el disco.
            if (estilo == 0) conRegistro(TrazosTinta.Tinta.MAGENTA, reg) {
                bandas(Offset(cd.x, r.top + r.height * 0.7f), r.height * 0.42f, 3, r.width * 0.07f, r.width * (0.02f + 0.04f * ii), r.width * 0.022f, tm, 1.3f, semilla * 0.13f, 0.8f)
            }
            if (med.bermellon > 0f || (!tinta && estilo == 1)) conRegistro(TrazosTinta.Tinta.BERMELLON, reg) {
                bandas(Offset(cd.x, r.top + r.height * 0.7f), r.height * 0.42f, 2, r.width * 0.11f, r.width * (0.02f + 0.04f * ii), r.width * 0.018f, tb.copy(alpha = if (tinta) med.bermellon else 1f), 1.3f, semilla * 0.13f + 0.2f, 0.9f)
            }
        }
        // El disco, sus anillos alternos (con la intensidad) y su etiqueta.
        val anillos = med.anillos.coerceIn(0, 8)
        for (k in anillos downTo 1) {
            val col = when (k % 3) { 0 -> tm; 1 -> tu; else -> if (med.bermellon > 0f) tb else tm }
            drawCircle(col, rd * (1f + 0.11f * k), cd, style = Stroke(r.width * 0.012f))
        }
        drawCircle(papel, rd * 1.04f, cd)
        drawCircle(if (tinta && o) lerp(NEGRO, CREMA, 0.85f) else grab, rd, cd)
        for (k in 1..4) drawCircle((if (tinta && o) NEGRO else papel).copy(alpha = 0.35f), rd * (0.5f + 0.12f * k), cd, style = Stroke(0.5f * u))
        conRegistro(TrazosTinta.Tinta.MAGENTA, reg) { drawCircle(tm, rd * 0.36f, cd) }
        drawCircle(papel, rd * 0.06f, cd)
        drawPath(marco, grab, style = Stroke(r.width * 0.014f))
        letrasBloque(Rect(r.left + r.width * 0.08f, r.bottom - r.height * 0.15f, r.right - r.width * 0.08f, r.bottom - r.height * 0.03f), grab, false, semilla)
    }

    /** Una fila de letras de bloque inventadas que llenan su forma (no se leen): bloques con huecos casi cerrados. */
    private fun DrawScope.letrasBloque(r: Rect, col: Color, lapiz: Boolean, semilla: Int) {
        val u = density
        val n = 5
        val gap = r.width * 0.025f
        val lw = (r.width - gap * (n - 1)) / n
        for (k in 0 until n) {
            val x = r.left + k * (lw + gap)
            // Alto en barril: más altas en el medio.
            val sube = r.height * 0.18f * (1f - sin(PI.toFloat() * (k + 0.5f) / n))
            val g = Rect(x, r.top + sube, x + lw, r.bottom - sube * 0.3f)
            val p = Path().apply {
                addRoundRect(RoundRect(g, CornerRadius(lw * 0.22f)))
            }
            // Los huecos: una ranura de lado y otra de arriba o de abajo, en sitios que cambian; a veces un punto. Así no se
            // parecen a ninguna letra de verdad.
            val hueco = Path().apply {
                val a1 = azar(semilla + 50, k); val a2 = azar(semilla + 60, k); val a3 = azar(semilla + 70, k)
                val yh = g.top + g.height * (0.22f + 0.22f * a1)
                if (a2 < 0.5f) addRect(Rect(g.left, yh, g.left + lw * (0.5f + 0.2f * a3), yh + g.height * 0.13f))
                else addRect(Rect(g.right - lw * (0.5f + 0.2f * a3), yh, g.right, yh + g.height * 0.13f))
                val xv = g.left + lw * (0.3f + 0.3f * a3)
                if (a1 < 0.5f) addRect(Rect(xv, g.bottom - g.height * 0.42f, xv + lw * 0.15f, g.bottom))
                else addRect(Rect(xv, g.top, xv + lw * 0.15f, g.top + g.height * 0.3f))
                if (a2 in 0.3f..0.7f) addRect(Rect(g.left + lw * (if (a3 < 0.5f) 0.66f else 0.18f), g.bottom - g.height * 0.3f, g.left + lw * (if (a3 < 0.5f) 0.8f else 0.32f), g.bottom - g.height * 0.17f))
            }
            val letra = Path.combine(PathOperation.Difference, p, hueco)
            if (lapiz) drawPath(letra, col, style = Stroke(0.6f * u)) else drawPath(letra, col)
        }
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val w = size.width; val alto = size.height
        if (w < 2f || alto < 2f) return
        empezar(oscuro, w, 0f, 0f, -1f)
        val ch = d.corona.height
        val b = d.base; val hb = b.height
        val sup = ch * SUP; val frente = ch * FRENTE; val cantoY = ch * CANTO
        val pieA = sup + (frente - sup) * 0.35f
        val pieF = sup + (frente - sup) * 0.85f
        val rincon = b.top + hb * RINCON
        val pisa = b.top + hb * PISA

        pared(w, ch, rincon)
        if (tinta && o) luces(w, 1f)
        val cartelR = Rect(w * 0.655f, w * 0.115f, w * 0.905f, w * 0.445f)
        cartelPared(cartelR)
        pase(Offset(w * 0.565f, w * 0.115f))
        // La proyección cae también sobre lo que hay en la pared: tenue, para que el cartel se siga viendo.
        if (tinta && o) clipRect(cartelR.left - w * 0.01f, cartelR.top - w * 0.01f, cartelR.right + w * 0.01f, cartelR.bottom + w * 0.01f) { luces(w, 0.28f) }
        suelo(w, rincon, pisa, alto)
        cajonera(d, w, ch, sup, frente, cantoY, b, pisa)

        // Lo de encima, de atrás adelante.
        lata(Offset(w * 0.875f, pieA), w)
        retroproyector(Offset(w * 0.665f, pieA), w, cantoY)
        tablero(w, pieF, pieA)
        lampara(w)
        frascos(Offset(w * 0.535f, pieF), w)
        cinta(Offset(w * 0.785f, pieF), w, frente, cantoY)

        // Al pie.
        tubo(w, b, pisa)
        bola(Offset(w * 0.69f, b.top + hb * 0.86f), w * 0.04f)
        papelera(w, b, pisa)

        // La placa de «y N más»: la tarjeta del portatarjetas del larguero. Sin ella, la cerradura de la planera.
        if (d.mas > 0) {
            val r = d.enBase(placa)
            portatarjetas(r, true)
        } else {
            val cc = d.enBase(placa).center
            val bo = Rect(cc.x - w * 0.016f, cc.y - w * 0.024f, cc.x + w * 0.016f, cc.y + w * 0.024f)
            drawRect(s(0.3f), bo.topLeft + Offset(1.5f * density, 2f * density), bo.size)
            pieza(redondeado(bo, 2f * density), Mat.LATON, bo, PLANO)
            drawCircle(c(Mat.HUECO, cc), w * 0.0045f, cc - Offset(0f, w * 0.005f))
            drawRect(c(Mat.HUECO, cc), Offset(cc.x - w * 0.002f, cc.y - w * 0.004f), Size(w * 0.004f, w * 0.012f))
        }

        // La luz de la lámpara, por encima de lo que tiene cerca (ciudad, de noche).
        if (o && !tinta) {
            val l = LAMP * w
            mancha(l + Offset(w * 0.02f, w * 0.16f), w * 0.34f, LAMPARA_LUZ.copy(alpha = 0.16f))
            mancha(l + Offset(0f, w * 0.02f), w * 0.09f, LAMPARA.copy(alpha = 0.32f))
        }
        when {
            !tinta -> granoMueble(Rect(0f, 0f, w, alto), o, if (o) 1.5f else 1.35f)
            !o -> granoMueble(Rect(0f, 0f, w, alto), false, 0.5f)
            else -> granoMueble(Rect(0f, 0f, w, alto), true, 0.6f)
        }
    }

    /**
     * La pared: el yeso pintado de verde salvia con su desgaste, la moldura alta y el friso crema; la mancha de la tarde arriba
     * a la izquierda (ciudad clara), el halo de la lámpara (ciudad oscura), el papel con trama de grabado (cartel claro) o el
     * negro de terciopelo (cartel oscuro). El rodapié a los lados, abajo.
     */
    private fun DrawScope.pared(w: Float, ch: Float, rincon: Float) {
        val u = density
        val alto = rincon
        val friso = w * 0.075f
        val zona = Rect(0f, 0f, w, alto)
        if (tinta) {
            if (!o) {
                drawRect(PAPEL_T, zona.topLeft, zona.size)
                // El tono de la pared, a trama de grabado fina; la mancha de la tarde se queda en papel.
                val sol = Path().apply {
                    moveTo(0f, friso); lineTo(w * 0.34f, friso); cubicTo(w * 0.3f, w * 0.2f, w * 0.18f, w * 0.34f, 0f, w * 0.44f); close()
                }
                val resto = Path.combine(PathOperation.Difference, caja(Rect(0f, friso, w, alto)), sol)
                clipPath(resto) { rayado(Rect(0f, friso, w, alto), 4.6f * u, 0.75f * u, GRABAR, 0f) }
                clipPath(sol) { rayado(Rect(0f, friso, w * 0.4f, w * 0.5f), 9f * u, 0.6f * u, GRABAR, 0f) }
            } else {
                drawRect(lerp(NEGRO, ULT, 0.07f), zona.topLeft, zona.size)
                drawRect(lerp(NEGRO, ULT, 0.11f), Offset.Zero, Size(w, friso))
            }
        } else {
            drawRect(c(Mat.PARED, Offset(w * 0.5f, w * 0.4f)), zona.topLeft, zona.size)
            drawRect(c(Mat.FRISO, Offset(w * 0.5f, 0f)), Offset.Zero, Size(w, friso))
            if (!o) {
                mancha(Offset(w * 0.1f, w * 0.18f), w * 0.62f, TrazosCiudad.AL_SOL.copy(alpha = 0.3f))
                mancha(Offset(w * 0.98f, w * 0.7f), w * 0.5f, TrazosCiudad.SOMBRA.copy(alpha = 0.14f))
            } else {
                val l = LAMP * w
                mancha(l, w * 0.62f, LAMPARA_LUZ.copy(alpha = 0.2f))
                mancha(l + Offset(0f, -w * 0.04f), w * 0.26f, LAMPARA_LUZ.copy(alpha = 0.22f))
                mancha(l + Offset(w * 0.02f, w * 0.1f), w * 0.2f, LAMPARA.copy(alpha = 0.18f))
            }
            // El desgaste: pinceladas de la pintura, desconchones que dejan ver el crema de debajo, una grieta y agujeros de clavo.
            pinceladas(Rect(0f, friso, w, alto), if (o) NOCHE else TrazosCiudad.AL_SOL, 41, 34, u, 0.07f)
            pinceladas(Rect(0f, friso, w, alto), TrazosCiudad.SOMBRA, 43, 22, u, 0.05f)
            val desconchon = c(Mat.FRISO, Offset(w * 0.2f, w * 0.5f))
            listOf(Triple(0.025f, 0.52f, 1.0f), Triple(0.96f, 0.6f, 0.8f), Triple(0.52f, 0.06f, 0.6f), Triple(0.015f, 0.83f, 0.7f)).forEachIndexed { i, (fx, fy, e) ->
                val r = Rect(w * fx - w * 0.022f * e, w * fy - w * 0.014f * e, w * fx + w * 0.022f * e, w * fy + w * 0.014f * e)
                val p = contornoIrregular(r, 70 + i, 14, 0.22f)
                drawPath(p, desconchon.copy(alpha = 0.85f))
                translate(0.8f * u, 0.8f * u) { drawPath(p, s(0.18f), style = Stroke(0.8f * u)) }
            }
            val grieta = Path().apply {
                moveTo(w * 0.995f, w * 0.09f); lineTo(w * 0.975f, w * 0.13f); lineTo(w * 0.982f, w * 0.16f); lineTo(w * 0.962f, w * 0.21f); lineTo(w * 0.968f, w * 0.24f)
            }
            drawPath(grieta, s(0.3f), style = Stroke(0.8f * u))
            for ((fx, fy) in listOf(0.45f to 0.12f, 0.6f to 0.4f, 0.93f to 0.47f)) {
                drawCircle(s(0.45f), 1.1f * u, Offset(w * fx, w * fy))
                drawCircle(b(0.5f, Offset(w * fx, w * fy)), 0.6f * u, Offset(w * fx - 0.6f * u, w * fy - 0.6f * u))
            }
        }
        // La moldura alta (para colgar cuadros), con su luz arriba y su sombra debajo.
        val mr = Rect(0f, friso, w, friso + w * 0.014f)
        drawRect(c(Mat.MOLDURA, mr.center), mr.topLeft, mr.size)
        drawRect(b(0.5f, mr.center), mr.topLeft, Size(w, 1.2f * u))
        drawRect(s(0.25f), Offset(0f, mr.bottom - 1.5f * u), Size(w, 1.5f * u))
        drawRect(s(0.12f), Offset(0f, mr.bottom), Size(w, 3f * u))
        if (tinta) { drawLine(if (o) CREMA.copy(alpha = 0.3f) else GRABAR, Offset(0f, mr.top), Offset(w, mr.top), 1f * u); drawLine(if (o) CREMA.copy(alpha = 0.3f) else GRABAR, Offset(0f, mr.bottom), Offset(w, mr.bottom), 1f * u) }
        // El rodapié a los lados de la cajonera.
        val rp = Rect(0f, rincon - w * 0.035f, w, rincon)
        pieza(caja(rp), Mat.MOLDURA, rp, TUMBADO, 0.7f)
    }

    /**
     * Las luces líquidas en la pared (cartel oscuro): tres manchas planas de tinta, de borde blando y vivo, con gotas sueltas;
     * donde se encima una con otra, el color de la sobreimpresión. Crecen y suben de croma con la intensidad; la de bermellón
     * aparece desde i = 0.5. Y el haz pálido que sube del retroproyector.
     */
    private fun DrawScope.luces(w: Float, fuerza: Float) {
        val crece = 1f + 0.45f * ii
        val cabeza = Offset(w * 0.672f, w * 0.205f)
        val haz = Path().apply { moveTo(cabeza.x - w * 0.012f, cabeza.y); lineTo(w * 0.25f, w * 0.02f); lineTo(w * 0.86f, w * 0.03f); lineTo(cabeza.x + w * 0.012f, cabeza.y); close() }
        drawPath(haz, CREMA.copy(alpha = (0.045f + 0.03f * ii) * fuerza))
        val lista = listOf(Triple(MANCHA_M, MAG, 0.19f), Triple(MANCHA_U, ULT, 0.17f), Triple(MANCHA_B, BER, 0.12f))
        lista.forEachIndexed { j, (cen, col, r0) ->
            val a = (if (j == 2) med.bermellon else 1f) * fuerza
            if (a <= 0f) return@forEachIndexed
            val rr = w * r0 * crece
            val p = gota(cen * w, rr, 300 + j * 17, 0.22f + 0.2f * ii)
            conRegistro(if (j == 0) TrazosTinta.Tinta.MAGENTA else if (j == 1) TrazosTinta.Tinta.ULTRAMAR else TrazosTinta.Tinta.BERMELLON, med.registro) {
                drawPath(p, col.copy(alpha = 0.78f * a))
                // Dentro, el corazón más vivo de la mancha y sus burbujas de aceite.
                drawPath(gota(cen * w + Offset(rr * 0.1f, -rr * 0.08f), rr * 0.55f, 330 + j, 0.3f), lerp(col, CREMA, 0.25f).copy(alpha = 0.7f * a))
                for (q in 0 until 4) {
                    val ang = azar(360 + j, q) * 6.28f
                    val rq = rr * (0.08f + 0.1f * azar(370 + j, q))
                    val pq = cen * w + Offset(cos(ang) * rr * (1.05f + 0.25f * azar(380 + j, q)), sin(ang) * rr * (1.05f + 0.25f * azar(380 + j, q)))
                    drawCircle(col.copy(alpha = 0.75f * a), rq, pq)
                }
                drawPath(p, CREMA.copy(alpha = 0.35f * a), style = Stroke(1.2f * density))
            }
        }
    }

    /** Una gota de aceite: un contorno blando de 9 puntos alrededor de [c], con su azar fijo. */
    private fun gota(c: Offset, r: Float, semilla: Int, irregular: Float): Path {
        val n = 9
        val pts = List(n) { k ->
            val a = 2f * PI.toFloat() * k / n + azar(semilla, k + 20) * 0.3f
            val rr = r * (1f - irregular + 2f * irregular * azar(semilla, k))
            Offset(c.x + cos(a) * rr, c.y + sin(a) * rr * 0.85f)
        }
        return Path().apply {
            moveTo((pts[0].x + pts[1].x) / 2, (pts[0].y + pts[1].y) / 2)
            for (k in 1..n) {
                val p1 = pts[k % n]; val p2 = pts[(k + 1) % n]
                val m = Offset((p1.x + p2.x) / 2, (p1.y + p2.y) / 2)
                val a0 = Offset((pts[k - 1].x + p1.x) / 2, (pts[k - 1].y + p1.y) / 2)
                cubicTo(a0.x + (p1.x - a0.x) * 0.66f, a0.y + (p1.y - a0.y) * 0.66f, m.x + (p1.x - m.x) * 0.66f, m.y + (p1.y - m.y) * 0.66f, m.x, m.y)
            }
            close()
        }
    }

    /** Una chincheta de cabeza redonda, de [col]. */
    private fun DrawScope.chincheta(p: Offset, r: Float, col: Mat) {
        drawCircle(s(0.35f), r, p + Offset(r * 0.6f, r * 0.7f))
        pieza(ovalo(Rect(p, r)), col, Rect(p, r), REDONDO)
        drawCircle(b(0.7f, p), r * 0.35f, p - Offset(r * 0.3f, r * 0.3f))
    }

    /** El cartel propio de David clavado en la pared, con su sombra, sus chinchetas y la esquina de abajo que se levanta. */
    private fun DrawScope.cartelPared(r: Rect) {
        val u = density
        val giro = 1.6f
        rotate(giro, r.center) {
            val hoja = caja(r)
            sombraEnPared(hoja, r.center, 0.22f)
            drawRect(c(Mat.PAPEL, r.center), r.topLeft, r.size)
            disenoCartel(Rect(r.left + r.width * 0.04f, r.top + r.width * 0.04f, r.right - r.width * 0.04f, r.bottom - r.width * 0.03f), false, 3, 1)
            drawRect(s(0.14f), Offset(r.right - r.width * 0.05f, r.top), Size(r.width * 0.05f, r.height))
            linea(hoja, r.center)
            // La esquina de abajo a la derecha, que se levanta del yeso.
            val e = r.width * 0.11f
            val dobla = poligono(r.right - e, r.bottom, r.right, r.bottom - e, r.right - e * 0.25f, r.bottom - e * 0.15f)
            drawPath(poligono(r.right - e, r.bottom, r.right, r.bottom - e, r.right, r.bottom), c(Mat.PARED, r.bottomRight).let { if (tinta && !o) PAPEL_T else it })
            pieza(dobla, Mat.MARFIL, Rect(r.right - e, r.bottom - e, r.right, r.bottom), TUMBADO)
            chincheta(Offset(r.left + r.width * 0.05f, r.top + r.width * 0.045f), 2.6f * u, Mat.ROJO)
            chincheta(Offset(r.right - r.width * 0.05f, r.top + r.width * 0.045f), 2.6f * u, Mat.AZUL)
            chincheta(Offset(r.left + r.width * 0.05f, r.bottom - r.width * 0.05f), 2.6f * u, Mat.CROMO)
        }
    }

    /** El pase de autobús de cartulina, prendido con una chincheta, un poco ladeado. */
    private fun DrawScope.pase(p: Offset) {
        val u = density
        val w = anchoW
        val r = Rect(p.x - w * 0.034f, p.y, p.x + w * 0.034f, p.y + w * 0.046f)
        rotate(-7f, r.center) {
            sombraEnPared(caja(r), r.center, 0.2f)
            pieza(caja(r), Mat.MARFIL, r)
            val franja = Rect(r.left, r.top + r.height * 0.18f, r.right, r.top + r.height * 0.36f)
            drawRect(c(Mat.NARANJA, franja.center), franja.topLeft, franja.size)
            for (k in 0 until 3) drawRect(c(Mat.TINTA, r.center).copy(alpha = 0.55f), Offset(r.left + r.width * 0.12f, r.top + r.height * (0.5f + 0.13f * k)), Size(r.width * (0.7f - 0.15f * k), 0.9f * u))
            // El taladro del revisor.
            drawCircle(c(Mat.PARED, r.center).let { if (tinta && !o) PAPEL_T else it }, 1.6f * u, Offset(r.right - r.width * 0.2f, r.bottom - r.height * 0.2f))
            chincheta(Offset(r.center.x, r.top + 2.5f * u), 2.2f * u, Mat.VERDE)
        }
    }

    /** El suelo de tabla: las tablas a lo ancho, sus juntas, las vetas y el desgaste delante. */
    private fun DrawScope.suelo(w: Float, rincon: Float, pisa: Float, alto: Float) {
        val u = density
        val r = Rect(0f, rincon, w, alto)
        drawRect(c(Mat.SUELO, Offset(w * 0.5f, (rincon + alto) / 2)), r.topLeft, r.size)
        val ancho = maxOf(w * 0.032f, 2f)
        var y = rincon
        var k = 0
        while (y < alto) {
            val h = ancho * (1f + 0.12f * k)
            val fila = Rect(0f, y, w, minOf(y + h, alto))
            if (tinta && !o) {
                if (k % 2 == 1) clipRect(fila.left, fila.top, fila.right, fila.bottom) { rayado(fila, 3.2f * u, 0.7f * u, GRABAR, 0f) }
                drawLine(GRABAR, Offset(0f, y), Offset(w, y), 1f * u)
            } else {
                drawRect(c(Mat.SUELO, fila.center).copy(alpha = 0.5f), fila.topLeft, fila.size)
                vetas(fila, s(if (o) 0.3f else 0.22f), 90 + k, densidad = 0.6f, grosor = 0.6f)
                drawRect(s(0.35f), Offset(0f, y), Size(w, 1.1f * u))
                drawRect(b(0.18f, fila.center), Offset(0f, y + 1.1f * u), Size(w, 0.8f * u))
            }
            // Las juntas de cabeza, corridas de una tabla a otra.
            var x = w * (0.13f + 0.27f * azar(95, k))
            while (x < w) {
                drawLine(if (tinta && !o) GRABAR else s(0.4f), Offset(x, y), Offset(x, minOf(y + h, alto)), 1f * u)
                x += w * 0.46f
            }
            y += h; k++
        }
        if (tinta && o) {
            // De noche, el suelo casi perdido: solo los filos de las tablas cerca del mueble.
            drawRect(NEGRO.copy(alpha = 0.55f), r.topLeft, r.size)
        }
        // La sombra de la cajonera en el suelo, a su pie.
        drawRect(s(0.35f), Offset(w * IZQ, pisa - 1f * u), Size(w * (DER - IZQ) + w * 0.02f, w * 0.022f))
        drawRect(s(0.15f), Offset(w * IZQ, pisa), Size(w * (DER - IZQ) + w * 0.04f, w * 0.05f))
    }

    /**
     * La cajonera: los costados de roble, el bastidor del frente con sus largueros entre fila y fila, el montante del medio,
     * el larguero de abajo con el portatarjetas y el zócalo; la tapa gruesa con su canto moldurado. Su sombra en la pared.
     * De noche, cada tramo del frente con la luz que le llega a su altura.
     */
    private fun DrawScope.cajonera(d: Disposicion, w: Float, ch: Float, sup: Float, frente: Float, cantoY: Float, b: Rect, pisa: Float) {
        val u = density
        val x0 = w * IZQ; val x1 = w * DER
        val abajo = b.top + b.height * LARGUERO
        val cuerpo = Rect(x0, cantoY, x1, abajo)
        // La sombra en la pared, a la derecha (de día y con la lámpara, que queda a la izquierda).
        if (!(tinta && o)) {
            drawRect(s(0.2f), Offset(x1, sup + w * 0.02f), Size(w * 0.022f, abajo - sup))
            drawRect(s(0.1f), Offset(x1 + w * 0.022f, sup + w * 0.04f), Size(w * 0.012f, abajo - sup))
        }
        // El frente, por tramos de altura (la luz baja por filas).
        val paso = maxOf(w * 0.02f, 2f)
        var y = cuerpo.top
        while (y < cuerpo.bottom) {
            val y1 = minOf(y + paso, cuerpo.bottom)
            val col = c(Mat.ROBLE, Offset(w * 0.3f, (y + y1) / 2))
            drawRect(col, Offset(x0, y), Size(x1 - x0, y1 - y + 0.5f))
            y = y1
        }
        if (!tinta) {
            vetas(cuerpo, s(if (o) 0.3f else 0.18f), 120, densidad = 0.35f, vertical = true, grosor = 0.7f)
            pinceladas(cuerpo, if (o) NOCHE else TrazosCiudad.AL_SOL, 121, 26, u, 0.06f)
        } else if (!o) {
            // En el cartel, el bastidor es ultramar plano con su trama en el costado derecho.
            clipRect(x1 - w * 0.04f, cuerpo.top, x1, cuerpo.bottom) { rayado(Rect(x1 - w * 0.04f, cuerpo.top, x1, cuerpo.bottom), 2.6f * u, 0.85f * u, GRABAR) }
        }
        // Los costados: el izquierdo a la luz (de día), el derecho en sombra; su canto de frente.
        val ci = Rect(x0, cuerpo.top, x0 + w * 0.012f, cuerpo.bottom)
        val cd = Rect(x1 - w * 0.012f, cuerpo.top, x1, cuerpo.bottom)
        drawRect(b(0.25f, ci.center), ci.topLeft, ci.size)
        drawRect(s(0.28f), cd.topLeft, cd.size)
        // El rebaje alrededor de cada hueco: la sombra del larguero de arriba y del montante de la izquierda.
        for (h in d.huecos) {
            drawRect(s(0.45f), Offset(h.left - 1.5f * u, h.top - 2f * u), Size(h.width + 3f * u, h.height + 4f * u))
            drawRect(b(0.3f, h.center), Offset(h.left - 1.5f * u, h.bottom + 2f * u), Size(h.width + 3f * u, 1f * u))
            if (tinta) marco(Rect(h.left - 1.5f * u, h.top - 2f * u, h.right + 1.5f * u, h.bottom + 2f * u), if (o) CREMA.copy(alpha = (0.12f + 0.35f * k(h.center)).coerceAtMost(0.5f)) else GRABAR, 1f * u)
        }
        // El larguero de abajo, con su moldura, y el zócalo rehundido.
        val larguero = Rect(x0, abajo - b.height * 0.32f, x1, abajo)
        drawRect(b(0.25f, larguero.center), Offset(x0, larguero.top), Size(x1 - x0, 1.2f * u))
        drawRect(s(0.12f), Offset(x0, larguero.bottom - 2f * u), Size(x1 - x0, 2f * u))
        val zocalo = Rect(x0 + w * 0.025f, abajo, x1 - w * 0.025f, pisa)
        pieza(caja(zocalo), Mat.NOGAL, zocalo, TUMBADO, 1.2f, c(Mat.NOGAL, zocalo.center).let { if (!tinta) lerp(it, hondo(it), 0.4f) else it })
        drawRect(s(0.45f), zocalo.topLeft, Size(zocalo.width, 3f * u))
        if (tinta) linea(caja(cuerpo), cuerpo.center, 1.3f)
        // La tapa: su cara de arriba (luz) y su canto grueso con la moldura.
        val tapa = Rect(w * 0.015f, sup, w * 0.985f, frente)
        val cara = Path().apply { moveTo(w * 0.03f, sup); lineTo(w * 0.97f, sup); lineTo(w * 0.985f, frente); lineTo(w * 0.015f, frente); close() }
        pieza(cara, Mat.ROBLE, tapa, TUMBADO, 0.6f, lerp(c(Mat.ROBLE, tapa.center), if (tinta) c(Mat.ROBLE, tapa.center) else TrazosCiudad.AL_SOL, if (!tinta && !o) 0.22f else 0f))
        if (!tinta) clipPath(cara) { vetas(tapa, s(0.2f), 130, densidad = 0.5f, grosor = 0.6f) }
        val cantoR = Rect(w * 0.015f, frente, w * 0.985f, cantoY)
        pieza(caja(cantoR), Mat.ROBLE, cantoR, TUMBADO, 1f)
        drawLine(s(0.35f), Offset(cantoR.left, cantoR.top + cantoR.height * 0.62f), Offset(cantoR.right, cantoR.top + cantoR.height * 0.62f), 1f * u)
        drawLine(b(0.35f, cantoR.center), Offset(cantoR.left, cantoR.top + cantoR.height * 0.66f), Offset(cantoR.right, cantoR.top + cantoR.height * 0.66f), 0.8f * u)
        drawRect(s(0.4f), Offset(x0, cantoY), Size(x1 - x0, 3f * u))
        // Un golpe en el canto y el desgaste de las manos en el borde.
        drawOval(s(0.25f), Offset(w * 0.62f, cantoR.top + cantoR.height * 0.3f), Size(w * 0.03f, cantoR.height * 0.25f))
    }

    /**
     * La mesa de dibujo inclinada de sobremesa: el tablero de abedul con su canto y su listón de abajo, el pie de apoyo detrás,
     * el cartel a medias pegado con cinta (la mitad izquierda entintada, la derecha a lápiz), la regla T cruzada y un lápiz en
     * el listón.
     */
    private fun DrawScope.tablero(w: Float, pieF: Float, pieA: Float) {
        val u = density
        val izq = w * 0.045f; val der = w * 0.5f
        val arriba = w * 0.2f
        val abajo = pieF - w * 0.012f
        // El pie de apoyo, detrás, a la derecha.
        val pata = poligono(der - w * 0.04f, arriba + w * 0.16f, der - w * 0.028f, arriba + w * 0.16f, der + w * 0.008f, pieA, der - w * 0.004f, pieA)
        pieza(pata, Mat.CROMO, Rect(der - w * 0.04f, arriba + w * 0.16f, der + w * 0.008f, pieA), PLANO)
        // La sombra del tablero en la pared.
        val tabla = poligono(izq + w * 0.012f, arriba, der - w * 0.012f, arriba, der, abajo, izq, abajo)
        sombraEnPared(tabla, Offset(der, arriba), 0.2f)
        contacto(Offset((izq + der) / 2, pieF), der - izq, w * 0.014f, 0.35f)
        // El tablero, con su canto derecho (el grosor) en sombra.
        val cantoD = poligono(der - w * 0.012f, arriba, der - w * 0.004f, arriba + w * 0.003f, der + w * 0.008f, abajo, der, abajo)
        pieza(cantoD, Mat.TABLERO, Rect(der - w * 0.012f, arriba, der + w * 0.008f, abajo), PLANO, 1f, c(Mat.TABLERO, Offset(der, abajo)).let { if (tinta) it else lerp(it, TrazosCiudad.SOMBRA, 0.35f) })
        val rt = Rect(izq, arriba, der, abajo)
        pieza(tabla, Mat.TABLERO, rt, PLANO, 0.8f)
        if (!tinta) clipPath(tabla) { vetas(rt, s(0.12f), 140, densidad = 0.4f, vertical = true, grosor = 0.6f) }
        // El cartel a medias, pegado con cinta en las esquinas.
        val pa = Rect(izq + w * 0.05f, arriba + w * 0.035f, der - w * 0.045f, abajo - w * 0.05f)
        val hoja = poligono(pa.left + w * 0.006f, pa.top, pa.right - w * 0.006f, pa.top, pa.right, pa.bottom, pa.left, pa.bottom)
        drawPath(hoja, s(0.18f))
        translate(-1f * u, -1.5f * u) { drawPath(hoja, c(Mat.PAPEL, pa.center)) }
        val corte = pa.left + pa.width * 0.56f
        clipPath(hoja) {
            clipRect(pa.left, pa.top, corte, pa.bottom) { disenoCartel(Rect(pa.left + pa.width * 0.04f, pa.top + pa.height * 0.03f, pa.right - pa.width * 0.04f, pa.bottom - pa.height * 0.02f), false, 7) }
            clipRect(corte, pa.top, pa.right, pa.bottom) { disenoCartel(Rect(pa.left + pa.width * 0.04f, pa.top + pa.height * 0.03f, pa.right - pa.width * 0.04f, pa.bottom - pa.height * 0.02f), true, 7) }
            // El borde de la tinta todavía fresca y la cuadrícula a lápiz de la parte sin hacer.
            val tr = if (tinta) (if (o) CREMA.copy(alpha = 0.25f) else GRABAR.copy(alpha = 0.3f)) else GRAFITO.copy(alpha = 0.3f)
            var gx = corte + pa.width * 0.1f
            while (gx < pa.right) { drawLine(tr, Offset(gx, pa.top), Offset(gx, pa.bottom), 0.5f * u); gx += maxOf(pa.width * 0.1f, 2f) }
        }
        linea(hoja, pa.center)
        // La luz de la lámpara, de noche, sobre el cartel.
        if (o && !tinta) clipPath(tabla) { mancha(Offset(w * 0.33f, w * 0.42f), w * 0.26f, LAMPARA.copy(alpha = 0.3f)) }
        // La cinta de pintor en las cuatro esquinas.
        for ((p, g) in listOf(pa.topLeft to -38f, pa.topRight to 38f, pa.bottomLeft to 35f, pa.bottomRight to -35f)) {
            rotate(g, p) {
                val r = Rect(p.x - w * 0.02f, p.y - w * 0.007f, p.x + w * 0.02f, p.y + w * 0.007f)
                pieza(caja(r), Mat.CINTA, r, TUMBADO, 0.6f, c(Mat.CINTA, p).copy(alpha = if (tinta) 1f else 0.92f))
            }
        }
        // El listón de abajo, donde se apoya el lápiz.
        val liston = Rect(izq - w * 0.004f, abajo - w * 0.012f, der + w * 0.004f, abajo + w * 0.004f)
        pieza(caja(liston), Mat.ROBLE, liston, TUMBADO)
        val lapiz = Rect(izq + w * 0.24f, liston.top - w * 0.008f, izq + w * 0.36f, liston.top)
        pieza(caja(lapiz), Mat.NARANJA, lapiz, TUMBADO)
        drawPath(poligono(lapiz.right, lapiz.top, lapiz.right + w * 0.014f, lapiz.center.y, lapiz.right, lapiz.bottom), c(Mat.MARFIL, lapiz.center))
        drawCircle(c(Mat.TINTA, lapiz.center), w * 0.002f, Offset(lapiz.right + w * 0.013f, lapiz.center.y))
        // La regla T: la cabeza por fuera del canto izquierdo y la hoja cruzando el cartel, con su borde de plástico y sus marcas.
        rotate(-2.5f, Offset(izq, w * 0.5f)) {
            val cabeza = Rect(izq - w * 0.022f, w * 0.44f, izq + w * 0.003f, w * 0.56f)
            val hojaT = Rect(cabeza.right, w * 0.49f, der - w * 0.035f, w * 0.508f)
            drawRect(s(0.3f), Offset(hojaT.left + w * 0.004f, hojaT.top + w * 0.007f), hojaT.size)
            drawRect(s(0.3f), Offset(cabeza.left + w * 0.004f, cabeza.top + w * 0.006f), cabeza.size)
            pieza(caja(hojaT), Mat.MARFIL, hojaT, TUMBADO, 0.8f, c(Mat.TABLERO, hojaT.center).let { if (tinta) c(Mat.MARFIL, hojaT.center) else lerp(it, TrazosCiudad.AL_SOL, if (o) 0f else 0.2f) })
            val borde = Rect(hojaT.left, hojaT.bottom - hojaT.height * 0.35f, hojaT.right, hojaT.bottom)
            drawRect(if (tinta) (if (o) CREMA.copy(alpha = 0.25f) else ULT) else c(Mat.VIDRIO, borde.center).copy(alpha = 0.7f), borde.topLeft, borde.size)
            var mx = hojaT.left + w * 0.01f
            var j = 0
            while (mx < hojaT.right - w * 0.005f) {
                drawLine(c(Mat.TINTA, hojaT.center).copy(alpha = 0.7f), Offset(mx, hojaT.top), Offset(mx, hojaT.top + hojaT.height * (if (j % 5 == 0) 0.55f else 0.3f)), 0.6f * u)
                mx += maxOf(w * 0.008f, 2f); j++
            }
            pieza(caja(cabeza), Mat.ROBLE, cabeza, PLANO, 1.2f)
            for (t in listOf(0.3f, 0.7f)) drawCircle(c(Mat.LATON, cabeza.center), w * 0.003f, Offset(cabeza.center.x, cabeza.top + cabeza.height * t))
        }
    }

    /**
     * La lámpara de brazo: la prensa en el canto de arriba del tablero, dos brazos con sus resortes (el codo cruza la moldura)
     * y la pantalla cónica de esmalte verde que se asoma sobre el cartel. De noche, en la ciudad, encendida: la bombilla en la
     * boca y el cono de luz que baja.
     */
    private fun DrawScope.lampara(w: Float) {
        val u = density
        val prensa = Offset(w * 0.07f, w * 0.205f)
        val codo = Offset(w * 0.14f, w * 0.04f)
        val cabeza = Offset(w * 0.29f, w * 0.115f)
        val eje = Offset(0.29f, 0.957f)
        val boca = cabeza + eje * (w * 0.082f)
        val nrm = Offset(-eje.y, eje.x)
        // La sombra de los brazos en la pared.
        if (!o || !tinta) {
            val sp = Path().apply { moveTo(prensa.x, prensa.y); lineTo(codo.x, codo.y); lineTo(cabeza.x, cabeza.y) }
            if (!o) translate(w * 0.02f, w * 0.014f) { drawPath(sp, s(0.18f), style = Stroke(w * 0.007f)) }
        }
        // La prensa.
        val pr = Rect(prensa.x - w * 0.016f, prensa.y - w * 0.01f, prensa.x + w * 0.016f, prensa.y + w * 0.028f)
        pieza(redondeado(pr, 2f * u), Mat.ESMALTE, pr, PLANO)
        drawLine(c(Mat.CROMO, pr.center), Offset(pr.center.x, pr.bottom), Offset(pr.center.x, pr.bottom + w * 0.01f), 2f * u)
        // Los brazos: dos varillas paralelas cada uno, con su resorte.
        fun brazo(a: Offset, z: Offset) {
            val dx = z.x - a.x; val dy = z.y - a.y
            val l = maxOf(sqrt(dx * dx + dy * dy), 1f)
            val n = Offset(-dy / l, dx / l) * (w * 0.004f)
            val col = c(Mat.ESMALTE, (a + z) / 2f)
            for (sgn in listOf(-1f, 1f)) drawLine(col, a + n * sgn, z + n * sgn, w * 0.004f, StrokeCap.Round)
            drawLine(b(0.5f, (a + z) / 2f), a - n, z - n, 0.8f * u)
            // El resorte: espiras chicas a lo largo del tercio del medio.
            val desde = a + Offset(dx, dy) * 0.3f
            val m = 9
            for (k in 0..m) {
                val p = desde + Offset(dx, dy) * (0.3f * k / m)
                drawLine(c(Mat.CROMO, p), p + n * 2.2f, p - n * 2.2f, 1f * u)
            }
            if (tinta && !o) for (sgn in listOf(-1f, 1f)) drawLine(GRABAR, a + n * sgn * 2f, z + n * sgn * 2f, 0.8f * u)
        }
        brazo(prensa, codo)
        brazo(codo, cabeza)
        drawCircle(c(Mat.CROMO, codo), w * 0.007f, codo)
        drawCircle(s(0.4f), w * 0.003f, codo)
        // La pantalla: un cono de la cabeza a la boca.
        val r0 = w * 0.014f; val r1 = w * 0.05f
        val pant = Path().apply {
            moveTo(cabeza.x + nrm.x * r0, cabeza.y + nrm.y * r0)
            lineTo(boca.x + nrm.x * r1, boca.y + nrm.y * r1)
            lineTo(boca.x - nrm.x * r1, boca.y - nrm.y * r1)
            lineTo(cabeza.x - nrm.x * r0, cabeza.y - nrm.y * r0)
            close()
        }
        val rp = Rect(minOf(boca.x - r1, cabeza.x - r0), cabeza.y - r0, maxOf(boca.x + r1, cabeza.x + r0), boca.y + r1 * 0.3f)
        if (!o) translate(w * 0.022f, w * 0.016f) { drawPath(pant, s(0.16f)) }
        // De noche (ciudad), el cono de luz que baja hacia el tablero.
        if (o && !tinta) {
            val cono = Path().apply {
                moveTo(boca.x + nrm.x * r1, boca.y + nrm.y * r1)
                lineTo(boca.x + eje.x * w * 0.24f + nrm.x * w * 0.15f, boca.y + eje.y * w * 0.24f + nrm.y * w * 0.15f)
                lineTo(boca.x + eje.x * w * 0.24f - nrm.x * w * 0.15f, boca.y + eje.y * w * 0.24f - nrm.y * w * 0.15f)
                lineTo(boca.x - nrm.x * r1, boca.y - nrm.y * r1)
                close()
            }
            drawPath(cono, LAMPARA.copy(alpha = 0.09f))
        }
        pieza(pant, Mat.ESMALTE, rp, REDONDO)
        // El remate de la cabeza y la perilla.
        drawCircle(c(Mat.ESMALTE, cabeza), r0 * 1.15f, cabeza)
        drawCircle(c(Mat.CROMO, cabeza), r0 * 0.45f, cabeza - nrm * (r0 * 1.4f))
        // La boca: el borde enrollado, y dentro, la bombilla (encendida de noche en la ciudad).
        val bocaR = Rect(boca.x - r1, boca.y - r1 * 0.32f, boca.x + r1, boca.y + r1 * 0.32f)
        rotate(-16.8f, boca) {
            val encendida = o && !tinta
            drawOval(if (encendida) LAMPARA else c(Mat.HUECO, boca), bocaR.topLeft, bocaR.size)
            if (encendida) { drawOval(Color(0xFFFFF4DC), Offset(boca.x - r1 * 0.45f, boca.y - r1 * 0.17f), Size(r1 * 0.9f, r1 * 0.34f)) }
            else drawOval(c(Mat.CROMO, boca).copy(alpha = 0.6f), Offset(boca.x - r1 * 0.35f, boca.y - r1 * 0.13f), Size(r1 * 0.7f, r1 * 0.26f))
            drawOval(c(Mat.CROMO, boca), bocaR.topLeft, bocaR.size, style = Stroke(1.6f * u))
            if (tinta) drawOval(if (o) CREMA.copy(alpha = 0.4f) else GRABAR, bocaR.topLeft, bocaR.size, style = Stroke(1f * u))
        }
        if (o && !tinta) {
            mancha(boca, w * 0.07f, Color(0xFFFFF0D0).copy(alpha = 0.45f))
        }
    }

    /**
     * El retroproyector de las luces líquidas: la caja con su platina de vidrio y su rejilla, la columna con la perilla de
     * enfoque, la cabeza con la lente y el espejo, los dos vidrios de reloj de aceite teñido en la platina y el cable que
     * cuelga por el canto. Apagado en la ciudad; encendido de noche en el cartel.
     */
    private fun DrawScope.retroproyector(pie: Offset, w: Float, cantoY: Float) {
        val u = density
        val encendido = tinta && o
        val cajaR = Rect(pie.x - w * 0.11f, pie.y - w * 0.086f, pie.x + w * 0.11f, pie.y)
        sombraEnPared(redondeado(cajaR, 3f * u), cajaR.center, 0.14f)
        contacto(Offset(cajaR.center.x, pie.y), cajaR.width * 1.05f, w * 0.014f, 0.35f)
        // El cable: sale de atrás, baja por el canto de la tapa y se pierde abajo.
        val cable = Path().apply {
            moveTo(cajaR.right - w * 0.02f, cajaR.bottom - w * 0.01f)
            cubicTo(cajaR.right + w * 0.02f, pie.y + w * 0.01f, cajaR.right + w * 0.03f, cantoY - w * 0.01f, cajaR.right + w * 0.035f, cantoY + w * 0.05f)
        }
        // La columna y la cabeza (detrás de la caja).
        val col = Rect(cajaR.right - w * 0.036f, w * 0.215f, cajaR.right - w * 0.017f, cajaR.top + w * 0.01f)
        pieza(caja(col), Mat.CROMO, col, REDONDO)
        val brazoC = Rect(pie.x - w * 0.01f, w * 0.205f, col.right, w * 0.218f)
        pieza(caja(brazoC), Mat.CHAPA, brazoC, TUMBADO)
        val cabezaR = Rect(pie.x - w * 0.04f, w * 0.18f, pie.x + w * 0.03f, w * 0.235f)
        pieza(redondeado(cabezaR, 2.5f * u), Mat.CHAPA, cabezaR, PLANO)
        // El espejo: la placa inclinada arriba, que mira a la pared.
        val espejo = poligono(cabezaR.left - w * 0.004f, cabezaR.top, cabezaR.right + w * 0.004f, cabezaR.top, cabezaR.right - w * 0.006f, cabezaR.top - w * 0.026f, cabezaR.left + w * 0.006f, cabezaR.top - w * 0.026f)
        pieza(espejo, Mat.TINTA, Rect(cabezaR.left, cabezaR.top - w * 0.026f, cabezaR.right, cabezaR.top), TUMBADO)
        if (encendido) drawPath(espejo, CREMA.copy(alpha = 0.55f), style = Stroke(1.4f * u))
        // La lente: un tubo corto bajo la cabeza.
        val lente = Rect(pie.x - w * 0.016f, cabezaR.bottom, pie.x + w * 0.012f, cabezaR.bottom + w * 0.026f)
        pieza(caja(lente), Mat.TINTA, lente, REDONDO)
        drawOval(if (encendido) CREMA else c(Mat.VIDRIO, lente.center), Rect(lente.left, lente.bottom - w * 0.004f, lente.right, lente.bottom + w * 0.004f).let { it.topLeft }, Size(lente.width, w * 0.008f))
        // La perilla de enfoque en la columna.
        drawCircle(c(Mat.TINTA, col.center), w * 0.009f, Offset(col.center.x, col.top + col.height * 0.45f))
        drawCircle(b(0.5f, col.center), w * 0.003f, Offset(col.center.x - w * 0.003f, col.top + col.height * 0.45f - w * 0.003f))
        // La caja: chapa beige con su rejilla, la perilla y el interruptor.
        pieza(redondeado(cajaR, 3f * u), Mat.CHAPA, cajaR, PLANO)
        val rejilla = Rect(cajaR.left + cajaR.width * 0.08f, cajaR.top + cajaR.height * 0.4f, cajaR.left + cajaR.width * 0.55f, cajaR.bottom - cajaR.height * 0.18f)
        var ry = rejilla.top
        while (ry < rejilla.bottom) {
            drawRect(if (encendido) lerp(BER, CREMA, 0.3f).copy(alpha = 0.6f) else s(0.4f), Offset(rejilla.left, ry), Size(rejilla.width, w * 0.0035f))
            ry += maxOf(w * 0.008f, 2f)
        }
        drawCircle(c(Mat.TINTA, cajaR.center), w * 0.008f, Offset(cajaR.right - cajaR.width * 0.2f, cajaR.top + cajaR.height * 0.55f))
        drawCircle(b(0.6f, cajaR.center), w * 0.0025f, Offset(cajaR.right - cajaR.width * 0.2f - w * 0.003f, cajaR.top + cajaR.height * 0.55f - w * 0.003f))
        val inter = Rect(cajaR.right - cajaR.width * 0.36f, cajaR.top + cajaR.height * 0.48f, cajaR.right - cajaR.width * 0.31f, cajaR.top + cajaR.height * 0.66f)
        drawRect(if (encendido) MAG else c(Mat.ROJO, inter.center), inter.topLeft, inter.size)
        // La platina de vidrio: una banda arriba, y sobre ella los dos vidrios de reloj con el aceite teñido.
        val platina = Rect(cajaR.left + w * 0.008f, cajaR.top - w * 0.006f, cajaR.right - w * 0.008f, cajaR.top + w * 0.008f)
        drawRect(if (encendido) lerp(CREMA, MAG, 0.15f) else c(Mat.VIDRIO, platina.center), platina.topLeft, platina.size)
        drawRect(b(0.5f, platina.center), platina.topLeft, Size(platina.width, 1f * u))
        if (tinta && !o) marco(platina, GRABAR, 1f * u)
        val vidrios = listOf(Triple(0.3f, Mat.ROSA, MAG), Triple(0.62f, Mat.AZUL, ULT))
        for ((fx, m, tn) in vidrios) {
            val cv = Offset(platina.left + platina.width * fx, platina.top + w * 0.002f)
            val rv = Rect(cv.x - w * 0.034f, cv.y - w * 0.009f, cv.x + w * 0.034f, cv.y + w * 0.009f)
            drawOval(s(0.25f), rv.topLeft + Offset(w * 0.003f, w * 0.004f), rv.size)
            drawOval(if (encendido) tn else c(m, cv).copy(alpha = if (tinta) 1f else 0.75f), rv.topLeft, rv.size)
            drawOval(if (encendido) CREMA.copy(alpha = 0.8f) else b(0.5f, cv), Offset(rv.left + rv.width * 0.2f, rv.top + rv.height * 0.15f), Size(rv.width * 0.3f, rv.height * 0.3f))
            drawOval(if (tinta) (if (o) CREMA.copy(alpha = 0.6f) else GRABAR) else c(Mat.CROMO, cv), rv.topLeft, rv.size, style = Stroke(0.9f * u))
        }
        drawPath(cable, c(Mat.TINTA, cajaR.center).let { if (tinta && o) lerp(it, CREMA, 0.2f) else it }, style = Stroke(w * 0.005f, cap = StrokeCap.Round))
        // El enchufe del cable, colgando.
        val fin = Offset(cajaR.right + w * 0.035f, cantoY + w * 0.05f)
        val enchufe = Rect(fin.x - w * 0.008f, fin.y, fin.x + w * 0.008f, fin.y + w * 0.02f)
        pieza(redondeado(enchufe, 2f * u), Mat.TINTA, enchufe, PLANO)
        drawLine(c(Mat.CROMO, fin), Offset(fin.x - w * 0.003f, enchufe.bottom), Offset(fin.x - w * 0.003f, enchufe.bottom + w * 0.008f), 1.4f * u)
        drawLine(c(Mat.CROMO, fin), Offset(fin.x + w * 0.003f, enchufe.bottom), Offset(fin.x + w * 0.003f, enchufe.bottom + w * 0.008f), 1.4f * u)
        if (encendido) {
            // La platina encendida alumbra lo que tiene alrededor.
            drawLine(CREMA.copy(alpha = 0.5f), Offset(platina.left, platina.top - 1f * u), Offset(platina.right, platina.top - 1f * u), 1.4f * u)
        }
    }

    /**
     * Dos frascos de tinta china: el de delante destapado, con la plumilla atravesada sobre la boca y el tapón tumbado al
     * lado; el de atrás, más chico, tapado. Vidrio oscuro de tinta, etiqueta de papel con su franja.
     */
    private fun DrawScope.frascos(pie: Offset, w: Float) {
        val u = density
        fun frasco(p: Offset, e: Float, tapado: Boolean, franja: Mat) {
            val cw = w * 0.042f * e; val chh = w * 0.046f * e
            val cuerpo = Rect(p.x - cw / 2, p.y - chh, p.x + cw / 2, p.y)
            contacto(Offset(p.x, p.y), cw * 1.4f, w * 0.01f, 0.4f)
            val forma = Path().apply {
                moveTo(cuerpo.left, cuerpo.bottom); lineTo(cuerpo.left, cuerpo.top + chh * 0.3f)
                cubicTo(cuerpo.left, cuerpo.top + chh * 0.12f, p.x - cw * 0.22f, cuerpo.top + chh * 0.08f, p.x - cw * 0.2f, cuerpo.top)
                lineTo(p.x + cw * 0.2f, cuerpo.top)
                cubicTo(p.x + cw * 0.22f, cuerpo.top + chh * 0.08f, cuerpo.right, cuerpo.top + chh * 0.12f, cuerpo.right, cuerpo.top + chh * 0.3f)
                lineTo(cuerpo.right, cuerpo.bottom); close()
            }
            pieza(forma, Mat.TINTA, cuerpo, REDONDO, 1.3f)
            // El brillo del vidrio.
            drawRect(if (tinta) (if (o) CREMA.copy(alpha = 0.5f * k(p) + 0.15f) else PAPEL_T) else b(0.7f, p).let { if (o) it else TrazosCiudad.AL_SOL.copy(alpha = 0.55f) }, Offset(cuerpo.left + cw * 0.14f, cuerpo.top + chh * 0.35f), Size(cw * 0.08f, chh * 0.5f))
            // La etiqueta.
            val et = Rect(cuerpo.left + cw * 0.08f, cuerpo.top + chh * 0.42f, cuerpo.right - cw * 0.08f, cuerpo.top + chh * 0.82f)
            pieza(caja(et), Mat.PAPEL, et, REDONDO, 0.8f)
            val fr = Rect(et.left, et.top + et.height * 0.2f, et.right, et.top + et.height * 0.45f)
            drawRect(c(franja, fr.center), fr.topLeft, fr.size)
            drawRect(c(Mat.TINTA, et.center).copy(alpha = 0.5f), Offset(et.left + et.width * 0.2f, et.top + et.height * 0.66f), Size(et.width * 0.6f, 0.9f * u))
            // El cuello y su tapón, o la boca abierta con el cerco de tinta.
            val cuello = Rect(p.x - cw * 0.2f, cuerpo.top - chh * 0.12f, p.x + cw * 0.2f, cuerpo.top + 1f)
            pieza(caja(cuello), Mat.TINTA, cuello, REDONDO)
            if (tapado) {
                val tapon = Rect(p.x - cw * 0.26f, cuello.top - chh * 0.22f, p.x + cw * 0.26f, cuello.top + chh * 0.02f)
                pieza(redondeado(tapon, 1.5f * u), Mat.ROJO, tapon, REDONDO)
                for (k in 0 until 4) drawLine(s(0.3f), Offset(tapon.left + tapon.width * (0.2f + 0.2f * k), tapon.top + 1f * u), Offset(tapon.left + tapon.width * (0.2f + 0.2f * k), tapon.bottom - 1f * u), 0.6f * u)
            } else {
                drawOval(c(Mat.TINTA, p).let { lerp(it, Color.Black, 0.3f) }, Offset(cuello.left, cuello.top - w * 0.003f), Size(cuello.width, w * 0.006f))
                drawOval(b(0.4f, p), Offset(cuello.left, cuello.top - w * 0.003f), Size(cuello.width, w * 0.006f), style = Stroke(0.7f * u))
            }
        }
        frasco(Offset(pie.x - w * 0.035f, pie.y - w * 0.018f), 0.82f, true, Mat.AZUL)
        frasco(pie, 1f, false, Mat.NARANJA)
        // El tapón tumbado, a la derecha.
        val tp = Rect(pie.x + w * 0.03f, pie.y - w * 0.012f, pie.x + w * 0.052f, pie.y)
        contacto(Offset(tp.center.x, pie.y), tp.width * 1.3f, w * 0.006f, 0.35f)
        pieza(redondeado(tp, 1.5f * u), Mat.ROJO, tp, TUMBADO)
        // La plumilla atravesada sobre la boca: el mango de madera y el plumín.
        val a = Offset(pie.x - w * 0.045f, pie.y - w * 0.075f)
        val z = Offset(pie.x + w * 0.05f, pie.y - w * 0.04f)
        drawLine(s(0.3f), a + Offset(w * 0.004f, w * 0.008f), z + Offset(w * 0.004f, w * 0.008f), w * 0.006f, StrokeCap.Round)
        drawLine(c(Mat.NOGAL, (a + z) / 2f), a, z, w * 0.006f, StrokeCap.Round)
        drawLine(b(0.5f, a), a + Offset(0f, -w * 0.0015f), z + Offset(0f, -w * 0.0015f), 0.8f * u)
        val pl = z + Offset(w * 0.014f, w * 0.005f)
        drawLine(c(Mat.CROMO, z), z, pl, w * 0.004f, StrokeCap.Round)
        drawLine(c(Mat.TINTA, pl), pl - Offset(w * 0.005f, w * 0.002f), pl + Offset(w * 0.004f, w * 0.0015f), 1.2f * u, StrokeCap.Round)
        if (tinta) drawLine(if (o) CREMA.copy(alpha = 0.4f) else GRABAR, a + Offset(0f, w * 0.003f), z + Offset(0f, w * 0.003f), 0.8f * u)
    }

    /**
     * La lata de café vacía con las plumillas y los pinceles: el cilindro de hojalata con sus aros, la etiqueta sin marca de
     * dos colores, y los mangos que asoman ladeados (alguno con la punta manchada de tinta).
     */
    private fun DrawScope.lata(pie: Offset, w: Float) {
        val u = density
        val lw = w * 0.07f; val lh = w * 0.1f
        val r = Rect(pie.x - lw / 2, pie.y - lh, pie.x + lw / 2, pie.y)
        sombraEnPared(caja(Rect(r.left, r.top - w * 0.08f, r.right, r.bottom)), r.center, 0.14f)
        contacto(Offset(pie.x, pie.y), lw * 1.35f, w * 0.012f, 0.35f)
        // Los mangos, detrás del borde.
        val mangos = listOf(Triple(-0.3f, -14f, Mat.ROJO), Triple(-0.1f, -5f, Mat.NOGAL), Triple(0.08f, 4f, Mat.VERDE), Triple(0.25f, 11f, Mat.AZUL), Triple(0.38f, 18f, Mat.TABLERO))
        mangos.forEachIndexed { k, (fx, g, m) ->
            val base = Offset(pie.x + lw * fx, r.top + w * 0.006f)
            val largo = w * (0.1f + 0.035f * azar(150, k))
            rotate(g, base) {
                val mango = Rect(base.x - w * 0.0045f, base.y - largo, base.x + w * 0.0045f, base.y)
                pieza(redondeado(mango, w * 0.004f), m, mango, REDONDO)
                // La virola de latón y la punta: pelo de pincel o plumín.
                val vir = Rect(mango.left - w * 0.0005f, mango.top - w * 0.012f, mango.right + w * 0.0005f, mango.top)
                pieza(caja(vir), Mat.CROMO, vir, REDONDO)
                if (k % 2 == 0) {
                    val pelo = poligono(vir.left, vir.top, vir.right, vir.top, base.x + w * 0.0015f, vir.top - w * 0.018f, base.x - w * 0.0015f, vir.top - w * 0.018f)
                    drawPath(pelo, if (k == 2) (if (tinta) MAG else c(Mat.ROSA, base)) else c(Mat.TINTA, base))
                } else {
                    drawLine(c(Mat.CROMO, base), Offset(base.x, vir.top), Offset(base.x, vir.top - w * 0.012f), 1.6f * u, StrokeCap.Round)
                }
            }
        }
        // El cilindro.
        val cuerpo = redondeado(r, 2f * u)
        pieza(cuerpo, Mat.CROMO, r, REDONDO, 1.2f)
        // La etiqueta sin marca: dos franjas y un disco.
        val et = Rect(r.left, r.top + lh * 0.2f, r.right, r.top + lh * 0.82f)
        pieza(caja(et), Mat.ROJO, et, REDONDO)
        val fr = Rect(et.left, et.top + et.height * 0.62f, et.right, et.top + et.height * 0.78f)
        drawRect(c(Mat.PAPEL, fr.center), fr.topLeft, fr.size)
        drawCircle(c(Mat.PAPEL, et.center), et.height * 0.2f, Offset(et.center.x - lw * 0.05f, et.top + et.height * 0.33f))
        drawCircle(c(Mat.ROJO, et.center), et.height * 0.1f, Offset(et.center.x - lw * 0.05f, et.top + et.height * 0.33f))
        // Los aros de la hojalata.
        for (fy in listOf(0.08f, 0.9f)) {
            drawLine(s(0.3f), Offset(r.left, r.top + lh * fy), Offset(r.right, r.top + lh * fy), 1f * u)
            drawLine(b(0.4f, r.center), Offset(r.left, r.top + lh * fy + 1f * u), Offset(r.right, r.top + lh * fy + 1f * u), 0.7f * u)
        }
        // La boca abierta.
        drawOval(c(Mat.HUECO, r.center), Offset(r.left + 1f * u, r.top - w * 0.004f), Size(lw - 2f * u, w * 0.009f))
        drawOval(c(Mat.CROMO, r.center), Offset(r.left, r.top - w * 0.005f), Size(lw, w * 0.01f), style = Stroke(1.2f * u))
    }

    /** El rollo de cinta de pintor, tumbado, con la punta que cae por el canto de la tapa. */
    private fun DrawScope.cinta(pie: Offset, w: Float, frente: Float, cantoY: Float) {
        val u = density
        val rx = w * 0.034f; val alto = w * 0.016f
        val top = Rect(pie.x - rx, pie.y - alto - rx * 0.32f, pie.x + rx, pie.y - alto + rx * 0.32f)
        val lado = Rect(pie.x - rx, top.center.y, pie.x + rx, pie.y)
        contacto(Offset(pie.x, pie.y), rx * 2.2f, w * 0.01f, 0.35f)
        // La punta que cuelga por el canto.
        val punta = poligono(pie.x + rx * 0.4f, pie.y - w * 0.003f, pie.x + rx * 0.4f + w * 0.018f, pie.y - w * 0.003f, pie.x + rx * 0.5f + w * 0.02f, cantoY + w * 0.018f, pie.x + rx * 0.5f + w * 0.002f, cantoY + w * 0.02f)
        val forma = Path().apply { addRect(lado); addOval(Rect(lado.left, lado.bottom - rx * 0.32f, lado.right, lado.bottom + rx * 0.32f)) }
        pieza(forma, Mat.CINTA, Rect(lado.left, lado.top, lado.right, lado.bottom + rx * 0.32f), REDONDO)
        pieza(ovalo(top), Mat.CINTA, top, TUMBADO, 0.5f, lerp(c(Mat.CINTA, top.center), if (tinta) c(Mat.CINTA, top.center) else TrazosCiudad.AL_SOL, if (!tinta && !o) 0.3f else 0f))
        // Las vueltas de la cinta y el hueco del cartón.
        drawOval(s(0.2f), Rect(top.left + rx * 0.2f, top.top + rx * 0.064f, top.right - rx * 0.2f, top.bottom - rx * 0.064f).topLeft, Size(rx * 1.6f, (top.height - rx * 0.128f)), style = Stroke(0.6f * u))
        val core = Rect(top.center.x - rx * 0.55f, top.center.y - rx * 0.17f, top.center.x + rx * 0.55f, top.center.y + rx * 0.17f)
        drawOval(c(Mat.KRAFT, core.center), core.topLeft, core.size)
        val hueco = Rect(core.left + core.width * 0.1f, core.top + core.height * 0.12f, core.right - core.width * 0.1f, core.bottom - core.height * 0.1f)
        drawOval(c(Mat.ROBLE, hueco.center).let { lerp(it, TrazosCiudad.SOMBRA, if (tinta) 0f else 0.35f) }, hueco.topLeft, hueco.size)
        pieza(punta, Mat.CINTA, Rect(pie.x + rx * 0.4f, pie.y, pie.x + rx * 0.5f + w * 0.02f, cantoY + w * 0.02f), PLANO, 0.8f)
    }

    /** El tubo de carteles de cartón, apoyado en el costado izquierdo, cortado por el borde, con su tapa de chapa. */
    private fun DrawScope.tubo(w: Float, b: Rect, pisa: Float) {
        val u = density
        val pieT = Offset(w * 0.018f, b.top + b.height * 0.93f)
        val arriba = Offset(w * 0.046f, b.top - w * 0.2f)
        val dx = arriba.x - pieT.x; val dy = arriba.y - pieT.y
        val ang = Math.toDegrees(kotlin.math.atan2(dx.toDouble(), -dy.toDouble())).toFloat()
        val largo = sqrt(dx * dx + dy * dy)
        val ancho = w * 0.038f
        rotate(ang, pieT) {
            val r = Rect(pieT.x - ancho / 2, pieT.y - largo, pieT.x + ancho / 2, pieT.y)
            translate(w * 0.012f, 0f) { drawRect(s(0.18f), r.topLeft, r.size) }
            pieza(caja(r), Mat.KRAFT, r, REDONDO)
            // La costura en espiral del cartón.
            var y = r.top + w * 0.03f
            while (y < r.bottom) { drawLine(s(0.25f), Offset(r.left, y), Offset(r.right, y - w * 0.02f), 0.8f * u); y += maxOf(w * 0.045f, 2f) }
            val tapa = Rect(r.left - w * 0.002f, r.top - w * 0.004f, r.right + w * 0.002f, r.top + w * 0.022f)
            pieza(caja(tapa), Mat.CROMO, tapa, REDONDO)
            // La etiqueta con las señas, ilegible.
            val et = Rect(r.left + ancho * 0.12f, r.top + largo * 0.25f, r.right - ancho * 0.12f, r.top + largo * 0.36f)
            pieza(caja(et), Mat.PAPEL, et, REDONDO, 0.7f)
            for (k in 0 until 3) drawLine(c(Mat.TINTA, et.center).copy(alpha = 0.5f), Offset(et.left + et.width * 0.2f, et.top + et.height * (0.25f + 0.25f * k)), Offset(et.right - et.width * 0.2f, et.top + et.height * (0.25f + 0.25f * k)), 0.7f * u)
        }
        contacto(Offset(pieT.x + w * 0.01f, pieT.y), w * 0.06f, w * 0.012f, 0.35f)
    }

    /** Un boceto arrugado en bola: el papel con sus pliegues y la tinta que asoma. */
    private fun DrawScope.bola(c0: Offset, r: Float, semilla: Int = 5) {
        val u = density
        contacto(Offset(c0.x, c0.y + r * 0.85f), r * 2.2f, r * 0.45f, 0.35f)
        val p = contornoIrregular(Rect(c0, r), semilla, 12, 0.14f)
        pieza(p, Mat.PAPEL, Rect(c0, r), REDONDO, 1.1f)
        for (k in 0 until 5) {
            val a = azar(semilla, k + 40) * 6.28f
            val q = c0 + Offset(cos(a) * r * 0.2f, sin(a) * r * 0.2f)
            drawLine(s(0.3f), q, q + Offset(cos(a + 1f) * r * 0.6f, sin(a + 1f) * r * 0.6f), 0.7f * u)
        }
        val rayon = Path().apply { moveTo(c0.x - r * 0.5f, c0.y + r * 0.1f); cubicTo(c0.x - r * 0.2f, c0.y - r * 0.3f, c0.x + r * 0.1f, c0.y + r * 0.3f, c0.x + r * 0.45f, c0.y - r * 0.1f) }
        drawPath(rayon, if (tinta) MAG else c(Mat.ROSA, c0), style = Stroke(r * 0.12f, cap = StrokeCap.Round))
    }

    /** La papelera de lata, cortada por el borde derecho, con los bocetos arrugados que rebosan. */
    private fun DrawScope.papelera(w: Float, b: Rect, pisa: Float) {
        val u = density
        val pieY = b.top + b.height * 0.97f
        val arriba = b.top + b.height * 0.3f
        val l = w * 0.83f; val r = w * 1.04f
        // Lo que rebosa, detrás del borde.
        bola(Offset(w * 0.875f, arriba - w * 0.012f), w * 0.033f, 11)
        bola(Offset(w * 0.94f, arriba - w * 0.02f), w * 0.036f, 13)
        // Un pliego con un trozo de cartel, que asoma de pie.
        val hoja = poligono(w * 0.9f, arriba + w * 0.01f, w * 0.915f, arriba - w * 0.07f, w * 0.97f, arriba - w * 0.06f, w * 0.96f, arriba + w * 0.01f)
        pieza(hoja, Mat.PAPEL, Rect(w * 0.9f, arriba - w * 0.07f, w * 0.97f, arriba + w * 0.01f), PLANO)
        clipPath(hoja) { drawCircle(if (tinta) ULT else c(Mat.AZUL, Offset(w * 0.93f, arriba - w * 0.04f)), w * 0.018f, Offset(w * 0.935f, arriba - w * 0.04f)) }
        contacto(Offset((l + r) / 2, pieY), r - l, w * 0.016f, 0.4f)
        val lata = poligono(l, arriba, r, arriba, r - w * 0.01f, pieY, l + w * 0.016f, pieY)
        pieza(lata, Mat.ESMALTE, Rect(l, arriba, r, pieY), REDONDO, 1.1f, c(Mat.ACERO, Offset((l + r) / 2, (arriba + pieY) / 2)))
        // Los aros y una abolladura.
        for (fy in listOf(0.06f, 0.5f, 0.94f)) {
            val y = arriba + (pieY - arriba) * fy
            drawLine(s(0.3f), Offset(l + w * 0.016f * fy, y), Offset(r, y), 1.2f * u)
            drawLine(b(0.4f, Offset(l, y)), Offset(l + w * 0.016f * fy, y + 1.2f * u), Offset(r, y + 1.2f * u), 0.7f * u)
        }
        drawOval(s(0.18f), Offset(l + w * 0.05f, arriba + (pieY - arriba) * 0.62f), Size(w * 0.04f, w * 0.03f))
        bola(Offset(w * 0.905f, arriba + w * 0.004f), w * 0.03f, 17)
    }

    // ---------------------------------------------------------------- los compartimentos

    /** Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece) para repartir bien las tres clases. */
    private fun mezclar(s: Int): Int {
        var x = s * 0x4FCC9A5DL.toInt()
        x = x xor (x ushr 15)
        x *= 0xC40F3609L.toInt()
        return x xor (x ushr 13)
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este hueco en el mueble, para saber cuánta luz le llega (la luz baja por filas).
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val dentro = c.cols == cols
        val wm = w / anchoH
        val x0 = (lado + c.col * (anchoH + hueco)) * wm
        val y0 = (corona + hueco + c.fila * (altoH + hueco)) * wm
        empezar(oscuro, wm, x0, y0, if (dentro) -1f else 0.6f)
        val m = mezclar(c.semilla)
        val tono = Math.floorMod(m shr 10, 3)
        clipRect(0f, 0f, w, h) {
            fondoHueco(w, h)
            if (c.vacio) { cajon(w, h, c, 0, 0f, vacio = true); return@clipRect }
            translate(0f, SALIDA * h * ab) {
                when {
                    c.sinTema -> recortes(w, h, c)
                    else -> when (Math.floorMod(m, 3)) {
                        0 -> cajon(w, h, c, tono, 0f)
                        1 -> entreabierto(w, h, c, tono)
                        else -> carpeta(w, h, c, tono)
                    }
                }
            }
        }
    }

    /** El fondo de un hueco: lo hondo de la cajonera, la sombra del larguero de arriba y el costado del lado contrario a la luz. */
    private fun DrawScope.fondoHueco(w: Float, h: Float) {
        val p = Offset(w / 2, h / 2)
        drawRect(c(Mat.HUECO, p), Offset.Zero, Size(w, h))
        if (tinta && !o) rayado(Rect(0f, 0f, w, h), 3f * density, 0.7f * density, PAPEL_T.copy(alpha = 0.25f), 0f)
        drawRect(s(0.35f), Offset.Zero, Size(w, h * 0.12f))
        drawRect(s(0.2f), Offset(if (luzIzq(p)) w * 0.9f else 0f, 0f), Size(w * 0.1f, h))
    }

    /** El material del frente de un cajón según el tono de la semilla: roble, nogal o acero pintado. */
    private fun frente(tono: Int): Mat = when (tono) { 0 -> Mat.ROBLE; 1 -> Mat.NOGAL; else -> Mat.ACERO }

    /** El color de la tarjeta del rótulo: el papel; de noche, más hundido cuanto más lejos de la luz (pero siempre legible). */
    private fun papelTarjeta(p: Offset): Color {
        if (!o) return if (tinta) PAPEL_T else lerp(PAPEL, Color(0xFFF7F0DE), 0.4f)
        val k = k(p)
        return if (tinta) lerp(PAPEL_LEJOS_TINTA, CREMA, (1.3f * k.pow(0.6f)).coerceAtMost(1f))
        else lerp(PAPEL_LEJOS_CIUDAD, lerp(PAPEL, LAMPARA, 0.12f), (1.3f * k.pow(0.6f)).coerceAtMost(1f))
    }

    /** El portatarjetas de latón con su tarjeta (vacía si [conTarjeta] es falso) y el labio para tirar, debajo. */
    private fun DrawScope.portatarjetas(r: Rect, conTarjeta: Boolean, tirador: Boolean = false) {
        val u = density
        val pr = Rect(r.left - 3.2f * u, r.top - 3.2f * u, r.right + 3.2f * u, r.bottom + 3.2f * u)
        drawRect(s(0.35f), Offset(pr.left + 1.5f * u, pr.top + 2f * u), pr.size)
        pieza(caja(pr), Mat.LATON, pr, PLANO)
        if (conTarjeta) {
            drawRect(papelTarjeta(r.center), r.topLeft, r.size)
            drawRect(s(0.12f), r.topLeft, Size(r.width, 2f * u))
        } else {
            // Sin tarjeta: se ve el frente, más limpio donde estuvo la tarjeta, y un resto de cinta.
            drawRect(s(0.16f), r.topLeft, r.size)
            drawRect(s(0.3f), r.topLeft, Size(r.width, 2.5f * u))
            val t = Rect(r.left + r.width * 0.06f, r.top + r.height * 0.3f, r.left + r.width * 0.16f, r.top + r.height * 0.62f)
            drawRect(c(Mat.CINTA, t.center).copy(alpha = if (tinta) 1f else 0.7f), t.topLeft, t.size)
            if (tinta) marco(t, if (o) CREMA.copy(alpha = 0.3f) else GRABAR, 0.7f * u)
        }
        // Los bordes doblados del marco, que sujetan la tarjeta por los lados.
        drawRect(c(Mat.LATON, pr.center), Offset(pr.left, r.top), Size(r.left - pr.left + 2f * u, r.height))
        drawRect(c(Mat.LATON, pr.center), Offset(r.right - 2f * u, r.top), Size(pr.right - r.right + 2f * u, r.height))
        drawRect(b(0.5f, pr.center), pr.topLeft, Size(pr.width, 1f * u))
        if (tinta) marco(pr, if (o) CREMA.copy(alpha = 0.45f) else GRABAR, 1f * u)
        // Los dos remaches.
        for (x in listOf(pr.left + 1.6f * u, pr.right - 1.6f * u)) {
            drawCircle(s(0.4f), 1.3f * u, Offset(x + 0.5f * u, pr.center.y + 0.6f * u))
            drawCircle(b(0.7f, pr.center), 0.8f * u, Offset(x, pr.center.y))
        }
        if (tirador) {
            // El labio para tirar, bajo el portatarjetas.
            val lb = Rect(pr.left + pr.width * 0.3f, pr.bottom, pr.right - pr.width * 0.3f, pr.bottom + 5.5f * u)
            val labio = Path().apply { moveTo(lb.left, lb.top); lineTo(lb.right, lb.top); cubicTo(lb.right, lb.bottom, lb.left + lb.width * 0.85f, lb.bottom, lb.center.x, lb.bottom); cubicTo(lb.left + lb.width * 0.15f, lb.bottom, lb.left, lb.bottom, lb.left, lb.top); close() }
            translate(1f * u, 2f * u) { drawPath(labio, s(0.3f)) }
            pieza(labio, Mat.LATON, lb, TUMBADO)
        }
    }

    /** La caja del rótulo en el compartimento, en píxeles. */
    private fun rotuloEn(w: Float, h: Float) = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)

    /**
     * Los pliegos de un cajón: el canto de cada uno, apilados entre [x0] y [x1] hasta [suelo], más altos con más fichas; el
     * de color de vez en cuando. Uno sobresale, ladeado, con un trozo de cartel impreso.
     */
    private fun DrawScope.pliegos(x0: Float, x1: Float, suelo: Float, h: Float, e: EstadoCajon, m: Int, sale: Boolean) {
        val u = density
        val n = e.n.coerceIn(0, 10)
        val cuantos = (2 + n).coerceIn(2, 11)
        val grueso = h * 0.021f
        val colores = listOf(Mat.PAPEL, Mat.MARFIL, Mat.PAPEL, Mat.GRIS, Mat.PAPEL, Mat.MARFIL, Mat.ROSA, Mat.PAPEL, Mat.AZUL, Mat.MARFIL, Mat.PAPEL)
        for (k in 0 until cuantos) {
            val y = suelo - (k + 1) * grueso
            val dx = (azar(e.semilla, k) - 0.5f) * (x1 - x0) * 0.03f
            val r = Rect(x0 + dx, y, x1 + dx, y + grueso)
            val col = colores[Math.floorMod(k + m, colores.size)]
            pieza(caja(r), col, r, TUMBADO, 0.7f)
        }
        if (!sale) return
        // El pliego que sobresale: más arriba, ladeado, con su esquina por fuera y un trozo de cartel.
        val izq = Math.floorMod(m shr 5, 2) == 0
        val top = suelo - cuantos * grueso - h * 0.03f
        val pw = (x1 - x0) * 0.46f
        val px = if (izq) x0 + (x1 - x0) * 0.04f else x1 - pw - (x1 - x0) * 0.04f
        val giro = if (izq) -4f else 4f
        rotate(giro, Offset(px + pw / 2, top)) {
            val r = Rect(px, top, px + pw, suelo - h * 0.005f)
            drawRect(s(0.3f), Offset(r.left + 2f * u, r.top + 2f * u), r.size)
            pieza(caja(r), Mat.PAPEL, r, PLANO, 0.6f)
            clipRect(r.left, r.top, r.right, r.bottom) {
                fragmento(r, izq, e.semilla)
            }
            linea(caja(r), r.center)
        }
    }

    /**
     * Un trozo de cartel impreso en un pliego, en [r]: tres bandas onduladas y el borde de un disco (nunca una diana). [izq]
     * pone el disco a la derecha.
     */
    private fun DrawScope.fragmento(r: Rect, izq: Boolean, semilla: Int) {
        val (tm, tu, tb) = tintasCartel(r.center)
        val cx = if (izq) r.right - r.width * 0.12f else r.left + r.width * 0.12f
        val cd = Offset(cx, r.bottom + r.height * 0.1f)
        val g = r.height * 0.13f
        for (k in 0 until 3) {
            val q = Path()
            val y0 = r.top + r.height * (0.25f + 0.24f * k)
            q.moveTo(r.left, y0)
            q.cubicTo(r.left + r.width * 0.3f, y0 - r.height * 0.3f, r.left + r.width * 0.6f, y0 + r.height * 0.3f, r.right, y0)
            val col = when ((k + semilla).mod(3)) { 0 -> tm; 1 -> tu; else -> if (med.bermellon > 0f || !tinta) tb else tm }
            drawPath(q, col, style = Stroke(g, cap = StrokeCap.Butt))
        }
        drawCircle(if (tinta) (if (o) CREMA else GRABAR) else c(Mat.TINTA, cd), r.height * 0.55f, cd)
        drawCircle(tm, r.height * 0.2f, cd)
    }

    /**
     * El cajón de la planera, cerrado o casi: el frente de roble, nogal o acero (según [tono]) con su bisel y su desgaste, el
     * portatarjetas con la tarjeta y el labio para tirar, y por arriba los pliegos que asoman (más con más fichas). Si es un
     * hueco libre ([vacio]), el portatarjetas vacío y nada asoma.
     */
    private fun DrawScope.cajon(w: Float, h: Float, e: EstadoCajon, tono: Int, baja: Float, vacio: Boolean = false) {
        val u = density
        val m = mezclar(e.semilla)
        val fr = Rect(w * 0.012f, h * 0.3f + baja, w * 0.988f, h * 0.99f + baja)
        if (!vacio) pliegos(w * 0.06f, w * 0.94f, fr.top + h * 0.02f, h, e, m, true)
        // El frente.
        val mat = frente(tono)
        drawRect(s(0.4f), Offset(fr.left, fr.top - 2f * u), Size(fr.width, 2f * u))
        pieza(caja(fr), mat, fr, PLANO)
        if (!tinta) {
            if (mat == Mat.ACERO) pinceladas(fr, if (o) NOCHE else TrazosCiudad.AL_SOL, e.semilla, 8, u, 0.1f)
            else vetas(fr, s(if (o) 0.25f else 0.16f), e.semilla, densidad = 0.45f, grosor = 0.7f)
        }
        // El filete del frente, el desgaste de las manos alrededor del tirador y un golpe.
        val fil = Rect(fr.left + w * 0.025f, fr.top + h * 0.06f, fr.right - w * 0.025f, fr.bottom - h * 0.06f)
        marco(fil, s(0.25f), 1f * u)
        marco(Rect(fil.left + 1f * u, fil.top + 1f * u, fil.right + 1f * u, fil.bottom + 1f * u), b(0.25f, fr.center), 0.7f * u)
        if (!tinta) drawOval(b(0.12f, fr.center), Offset(w * 0.32f, h * 0.88f + baja), Size(w * 0.36f, h * 0.08f))
        drawOval(s(0.18f), Offset(fr.left + fr.width * (0.08f + 0.75f * azar(m, 3)), fr.top + fr.height * 0.12f), Size(w * 0.03f, h * 0.03f))
        val rr = rotuloEn(w, h).let { Rect(it.left, it.top + baja, it.right, it.bottom + baja) }
        portatarjetas(rr, !vacio, tirador = true)
    }

    /**
     * El cajón entreabierto: el frente sacado hacia quien mira, los costados del cajón a la vista, los pliegos dentro (más con
     * más fichas) y una regla de madera o una escuadra de plástico que asoma por encima del frente.
     */
    private fun DrawScope.entreabierto(w: Float, h: Float, e: EstadoCajon, tono: Int) {
        val u = density
        val m = mezclar(e.semilla)
        val fondoY = h * 0.1f
        val frY = h * 0.43f
        // La caja del cajón, por dentro: el fondo, los costados en trapecio y la luz de arriba.
        val dentroP = poligono(w * 0.06f, fondoY, w * 0.94f, fondoY, w, frY, 0f, frY)
        drawPath(dentroP, c(Mat.TABLERO, Offset(w / 2, h * 0.25f)).let { if (tinta) it else lerp(it, TrazosCiudad.SOMBRA, if (o) 0f else 0.25f) })
        // Los pliegos dentro, vistos desde arriba: la cara del de encima y los cantos.
        val n = e.n.coerceIn(0, 10)
        val alza = h * (0.015f + 0.014f * n)
        val cara = poligono(w * 0.1f, fondoY + h * 0.05f - alza, w * 0.9f, fondoY + h * 0.05f - alza, w * 0.95f, frY - h * 0.02f - alza, w * 0.05f, frY - h * 0.02f - alza)
        val cantos = Rect(w * 0.05f, frY - h * 0.02f - alza, w * 0.95f, frY)
        pieza(caja(cantos), Mat.MARFIL, cantos, TUMBADO)
        var y = cantos.top + 1.6f * u
        while (y < cantos.bottom) { drawLine(s(0.25f), Offset(cantos.left, y), Offset(cantos.right, y), 0.5f * u); y += maxOf(h * 0.014f, 1.5f) }
        pieza(cara, Mat.PAPEL, Rect(w * 0.05f, fondoY - alza, w * 0.95f, frY - alza), TUMBADO, 0.4f)
        clipPath(cara) {
            // Lo que se ve del de encima: un cartel a medias, a lápiz con una banda entintada.
            fragmento(Rect(w * 0.4f, fondoY - alza, w * 0.95f, frY - alza), false, e.semilla)
            drawLine(if (tinta) (if (o) CREMA.copy(alpha = 0.4f) else GRABAR) else GRAFITO.copy(alpha = 0.5f), Offset(w * 0.15f, h * 0.22f - alza), Offset(w * 0.45f, h * 0.22f - alza), 0.7f * u)
        }
        // La sombra del larguero de arriba sobre lo de dentro.
        drawRect(s(0.35f), Offset(0f, 0f), Size(w, fondoY + h * 0.04f))
        // Los costados del cajón.
        val ci = poligono(0f, frY, w * 0.06f, fondoY, w * 0.075f, fondoY, w * 0.022f, frY)
        val cd2 = poligono(w, frY, w * 0.94f, fondoY, w * 0.925f, fondoY, w * 0.978f, frY)
        pieza(ci, Mat.TABLERO, Rect(0f, fondoY, w * 0.075f, frY), PLANO)
        pieza(cd2, Mat.TABLERO, Rect(w * 0.925f, fondoY, w, frY), PLANO, 1f, c(Mat.TABLERO, Offset(w, frY)).let { if (tinta) it else lerp(it, TrazosCiudad.SOMBRA, 0.3f) })
        // La regla (de madera, con sus marcas) o la escuadra (de plástico), que asoma por encima del frente.
        val escuadra = Math.floorMod(m shr 7, 2) == 1
        if (!escuadra) {
            rotate(-9f, Offset(w * 0.5f, frY)) {
                val rg = Rect(w * 0.18f, frY - h * 0.12f, w * 0.92f, frY - h * 0.07f)
                drawRect(s(0.3f), Offset(rg.left + 2f * u, rg.top + 3f * u), rg.size)
                pieza(caja(rg), Mat.TABLERO, rg, TUMBADO, 1f, c(Mat.MARFIL, rg.center))
                var mx = rg.left + w * 0.02f
                var j = 0
                while (mx < rg.right - w * 0.01f) {
                    drawLine(c(Mat.TINTA, rg.center).copy(alpha = 0.7f), Offset(mx, rg.top), Offset(mx, rg.top + rg.height * (if (j % 5 == 0) 0.6f else 0.3f)), 0.6f * u)
                    mx += maxOf(w * 0.018f, 2f); j++
                }
            }
        } else {
            val tri = poligono(w * 0.55f, frY - h * 0.02f, w * 0.93f, frY - h * 0.02f, w * 0.6f, frY - h * 0.3f)
            val huecoT = poligono(w * 0.62f, frY - h * 0.06f, w * 0.81f, frY - h * 0.06f, w * 0.635f, frY - h * 0.2f)
            val placaT = Path.combine(PathOperation.Difference, tri, huecoT)
            translate(2f * u, 3f * u) { drawPath(placaT, s(0.22f)) }
            val col = if (tinta) (if (o) lerp(NEGRO, BER, 0.3f + 0.5f * k(Offset(w * 0.7f, h * 0.2f))) else CALIDA) else c(Mat.NARANJA, Offset(w * 0.7f, h * 0.2f)).copy(alpha = 0.72f)
            drawPath(placaT, col)
            drawPath(placaT, if (tinta) (if (o) CREMA.copy(alpha = 0.5f) else GRABAR) else b(0.5f, Offset(w * 0.7f, h * 0.2f)), style = Stroke(1f * u))
        }
        // El frente, sacado: más abajo y un poco más ancho.
        val fr = Rect(-w * 0.005f, frY, w * 1.005f, h * 1.02f)
        val mat = frente(tono)
        drawRect(s(0.45f), Offset(fr.left, fr.top + h * 0.02f), Size(fr.width, h * 0.04f))
        pieza(caja(fr), mat, fr, PLANO)
        if (!tinta) {
            if (mat == Mat.ACERO) pinceladas(fr, if (o) NOCHE else TrazosCiudad.AL_SOL, e.semilla, 8, u, 0.1f)
            else vetas(fr, s(if (o) 0.25f else 0.16f), e.semilla, densidad = 0.45f, grosor = 0.7f)
        }
        drawRect(b(0.4f, fr.center), fr.topLeft, Size(fr.width, 1.5f * u))
        portatarjetas(rotuloEn(w, h), true, tirador = true)
    }

    /**
     * Lo que se mete en un hueco sin cajón: una caja plana de papel fotográfico (la tapa de color, naranja quemado o verde
     * botella, que se levanta un poco con las hojas que asoman) o un portafolios de cartón (estraza, gris o negro) atado con
     * su cinta en lazo, más grueso con más fichas. La tarjeta va pegada al frente.
     */
    private fun DrawScope.carpeta(w: Float, h: Float, e: EstadoCajon, tono: Int) {
        val u = density
        val n = e.n.coerceIn(0, 10)
        val piso = h * 0.95f
        // El piso del hueco, con su canto.
        drawRect(c(Mat.ROBLE, Offset(w / 2, piso)), Offset(0f, piso), Size(w, h - piso))
        drawRect(b(0.4f, Offset(w / 2, piso)), Offset(0f, piso), Size(w, 1f * u))
        if (tono == 1) {
            // El portafolios atado: dos tapas de cartón con los pliegos entre medias.
            val grueso = h * (0.5f + 0.035f * n).coerceAtMost(0.8f)
            val r = Rect(w * 0.035f, piso - grueso, w * 0.965f, piso)
            contacto(Offset(w / 2, piso), w * 0.95f, h * 0.06f, 0.4f)
            val tapaMat = when (Math.floorMod(mezclar(e.semilla) shr 3, 3)) { 0 -> Mat.KRAFT; 1 -> Mat.GRIS; else -> Mat.VERDE }
            val tapaArr = Rect(r.left - w * 0.01f, r.top, r.right + w * 0.005f, r.top + h * 0.07f)
            val tapaAb = Rect(r.left - w * 0.005f, r.bottom - h * 0.07f, r.right + w * 0.01f, r.bottom)
            val medio = Rect(r.left + w * 0.02f, tapaArr.bottom, r.right - w * 0.01f, tapaAb.top)
            pieza(caja(medio), Mat.PAPEL, medio, PLANO, 0.6f)
            var y = medio.top + 2f * u
            var j = 0
            while (y < medio.bottom) { drawLine(if (j % 4 == 2) c(Mat.ROSA, medio.center) else s(0.25f), Offset(medio.left, y), Offset(medio.right - w * 0.01f * azar(e.semilla, j), y), 0.7f * u); y += maxOf(h * 0.02f, 1.5f); j++ }
            pieza(caja(tapaArr), tapaMat, tapaArr, TUMBADO)
            pieza(caja(tapaAb), tapaMat, tapaAb, TUMBADO)
            // La cinta, atada en lazo arriba a la izquierda.
            val cintaMat = when (Math.floorMod(mezclar(e.semilla) shr 12, 3)) { 0 -> Mat.ROJO; 1 -> Mat.VERDE; else -> Mat.MARFIL }
            val cx = w * 0.13f
            val banda = Rect(cx - w * 0.014f, r.top - h * 0.01f, cx + w * 0.014f, r.bottom)
            pieza(caja(banda), cintaMat, banda, PLANO)
            val lazo1 = ovalo(Rect(cx - w * 0.07f, r.top - h * 0.08f, cx, r.top + h * 0.02f))
            val lazo2 = ovalo(Rect(cx, r.top - h * 0.07f, cx + w * 0.065f, r.top + h * 0.025f))
            pieza(lazo1, cintaMat, Rect(cx - w * 0.07f, r.top - h * 0.08f, cx, r.top + h * 0.02f), REDONDO)
            pieza(lazo2, cintaMat, Rect(cx, r.top - h * 0.07f, cx + w * 0.065f, r.top + h * 0.025f), REDONDO)
            drawCircle(c(cintaMat, Offset(cx, r.top)), w * 0.014f, Offset(cx, r.top - h * 0.02f))
            val cabo = poligono(cx - w * 0.005f, r.top, cx + w * 0.008f, r.top, cx + w * 0.03f, r.top + h * 0.16f, cx + w * 0.016f, r.top + h * 0.17f)
            pieza(cabo, cintaMat, Rect(cx - w * 0.005f, r.top, cx + w * 0.03f, r.top + h * 0.17f), PLANO)
            val rr = rotuloEn(w, h)
            drawRect(s(0.3f), Offset(rr.left + 1.5f * u, rr.top + 2f * u), rr.size)
            drawRect(papelTarjeta(rr.center), rr.topLeft, rr.size)
            linea(caja(rr), rr.center, 0.8f)
            // Dos tiras de cinta de pintor que sujetan la tarjeta.
            for ((p, g) in listOf(Offset(rr.left + w * 0.01f, rr.top) to -30f, Offset(rr.right - w * 0.01f, rr.bottom) to -30f)) rotate(g, p) {
                val t = Rect(p.x - w * 0.03f, p.y - h * 0.02f, p.x + w * 0.03f, p.y + h * 0.02f)
                drawRect(c(Mat.CINTA, p).copy(alpha = if (tinta) 1f else 0.9f), t.topLeft, t.size)
                if (tinta) marco(t, if (o) CREMA.copy(alpha = 0.3f) else GRABAR, 0.7f * u)
            }
        } else {
            // La caja de papel fotográfico: el cuerpo negro mate y la tapa de color, levantada por las hojas.
            val tapaMat = if (tono == 0) Mat.NARANJA else Mat.VERDE
            val cuerpo = Rect(w * 0.04f, h * 0.48f, w * 0.96f, piso)
            contacto(Offset(w / 2, piso), w * 0.95f, h * 0.06f, 0.4f)
            pieza(caja(cuerpo), Mat.TINTA, cuerpo, PLANO, 1f, c(Mat.GRIS, cuerpo.center).let { if (tinta) c(Mat.TINTA, cuerpo.center) else lerp(it, TINTA, if (o) 0.4f else 0.55f) })
            // Las hojas que asoman entre la tapa y la caja.
            val alza = h * (0.02f + 0.012f * n)
            val hojas = Rect(cuerpo.left + w * 0.02f, cuerpo.top - alza - h * 0.02f, cuerpo.right - w * 0.02f, cuerpo.top + h * 0.02f)
            pieza(caja(hojas), Mat.PAPEL, hojas, TUMBADO, 0.6f)
            var y = hojas.top + 1.5f * u
            while (y < hojas.bottom) { drawLine(s(0.22f), Offset(hojas.left, y), Offset(hojas.right, y), 0.5f * u); y += maxOf(h * 0.012f, 1.5f) }
            // La tapa: su cara de arriba (vista un poco desde arriba) y su faldón con la franja.
            val tapaTop = h * 0.17f - alza
            val faldon = Rect(w * 0.025f, tapaTop + h * 0.1f, w * 0.975f, tapaTop + h * 0.3f)
            val cara = poligono(w * 0.06f, tapaTop, w * 0.94f, tapaTop, faldon.right, faldon.top, faldon.left, faldon.top)
            pieza(cara, tapaMat, Rect(faldon.left, tapaTop, faldon.right, faldon.top), TUMBADO, 0.5f, lerp(c(tapaMat, faldon.center), if (tinta) c(tapaMat, faldon.center) else TrazosCiudad.AL_SOL, if (!tinta && !o) 0.25f else 0f))
            pieza(caja(faldon), tapaMat, faldon, PLANO)
            val franja = Rect(faldon.left, faldon.top + faldon.height * 0.55f, faldon.right, faldon.top + faldon.height * 0.75f)
            drawRect(c(Mat.PAPEL, franja.center), franja.topLeft, franja.size)
            if (tinta) marco(franja, if (o) CREMA.copy(alpha = 0.3f) else GRABAR, 0.7f * u)
            drawRect(s(0.35f), Offset(faldon.left, faldon.bottom), Size(faldon.width, 2.5f * u))
            // Una esquina de la tapa rota por el uso.
            drawPath(poligono(faldon.right - w * 0.03f, faldon.bottom, faldon.right, faldon.bottom, faldon.right, faldon.bottom - h * 0.06f), c(Mat.GRIS, faldon.center))
            val rr = rotuloEn(w, h)
            drawRect(s(0.3f), Offset(rr.left + 1.5f * u, rr.top + 2f * u), rr.size)
            drawRect(papelTarjeta(rr.center), rr.topLeft, rr.size)
            marco(Rect(rr.left + 2.5f * u, rr.top + 2.5f * u, rr.right - 2.5f * u, rr.bottom - 2.5f * u), c(tapaMat, rr.center).copy(alpha = 0.6f), 0.8f * u)
            linea(caja(rr), rr.center, 0.8f)
        }
    }

    /**
     * Las ideas sin tema: la caja de cartón de los recortes, abierta, con las solapas de atrás levantadas y los recortes que
     * asoman revueltos (más con más fichas): papel de cartel, un trozo de periódico, una tira de color. La etiqueta, escrita a
     * mano en un papel pegado con cinta.
     */
    private fun DrawScope.recortes(w: Float, h: Float, e: EstadoCajon) {
        val u = density
        val n = e.n.coerceIn(0, 10)
        val boca = h * 0.4f
        val piso = h * 0.97f
        // Las solapas de atrás, levantadas y abiertas hacia fuera.
        val sol1 = poligono(w * 0.06f, boca, w * 0.48f, boca, w * 0.42f, h * 0.1f, w * 0.0f, h * 0.16f)
        val sol2 = poligono(w * 0.52f, boca, w * 0.94f, boca, w * 1.0f, h * 0.14f, w * 0.58f, h * 0.08f)
        pieza(sol1, Mat.KRAFT, Rect(0f, h * 0.1f, w * 0.48f, boca), PLANO, 1f, lerp(c(Mat.KRAFT, Offset(w * 0.25f, h * 0.25f)), if (tinta) c(Mat.KRAFT, Offset(w * 0.25f, h * 0.25f)) else TrazosCiudad.SOMBRA, if (tinta) 0f else 0.25f))
        pieza(sol2, Mat.KRAFT, Rect(w * 0.52f, h * 0.08f, w, boca), PLANO, 1f, lerp(c(Mat.KRAFT, Offset(w * 0.75f, h * 0.25f)), if (tinta) c(Mat.KRAFT, Offset(w * 0.75f, h * 0.25f)) else TrazosCiudad.SOMBRA, if (tinta) 0f else 0.4f))
        // Los recortes.
        val cuantos = (3 + n).coerceIn(3, 9)
        val mats = listOf(Mat.PAPEL, Mat.ROSA, Mat.MARFIL, Mat.AZUL, Mat.PAPEL, Mat.NARANJA, Mat.GRIS, Mat.PAPEL, Mat.VERDE)
        for (k in 0 until cuantos) {
            val fx = 0.1f + 0.8f * (k + 0.5f) / cuantos + (azar(e.semilla, k) - 0.5f) * 0.08f
            val rw = w * (0.12f + 0.1f * azar(e.semilla, k + 10))
            val rh = h * (0.2f + 0.18f * azar(e.semilla, k + 20))
            val g = (azar(e.semilla, k + 30) - 0.5f) * 40f
            val base = Offset(w * fx, boca + h * 0.08f)
            rotate(g, base) {
                val r = Rect(base.x - rw / 2, base.y - rh, base.x + rw / 2, base.y)
                drawRect(s(0.25f), Offset(r.left + 1.5f * u, r.top + 1.5f * u), r.size)
                val mat = mats[k % mats.size]
                pieza(caja(r), mat, r, PLANO, 0.6f)
                if (mat == Mat.GRIS || mat == Mat.MARFIL) for (j in 0 until 4) drawLine(c(Mat.TINTA, r.center).copy(alpha = 0.45f), Offset(r.left + r.width * 0.12f, r.top + r.height * (0.2f + 0.16f * j)), Offset(r.right - r.width * 0.12f, r.top + r.height * (0.2f + 0.16f * j)), 0.7f * u)
                if (mat == Mat.PAPEL && k % 2 == 0) drawCircle(if (tinta) MAG else c(Mat.ROSA, r.center), r.width * 0.22f, Offset(r.center.x, r.top + r.height * 0.35f))
            }
        }
        // El frente de la caja, con su solapa de delante doblada hacia fuera.
        val fr = Rect(w * 0.03f, boca, w * 0.97f, piso)
        contacto(Offset(w / 2, piso), w * 0.95f, h * 0.06f, 0.4f)
        pieza(caja(fr), Mat.KRAFT, fr, PLANO)
        val solF = poligono(fr.left, fr.top, fr.right, fr.top, fr.right - w * 0.02f, fr.top + h * 0.1f, fr.left + w * 0.02f, fr.top + h * 0.1f)
        pieza(solF, Mat.KRAFT, Rect(fr.left, fr.top, fr.right, fr.top + h * 0.1f), TUMBADO, 1f, lerp(c(Mat.KRAFT, fr.center), if (tinta) c(Mat.KRAFT, fr.center) else TrazosCiudad.AL_SOL, if (!tinta && !o) 0.2f else 0f))
        drawLine(s(0.4f), Offset(fr.left, fr.top + h * 0.1f), Offset(fr.right, fr.top + h * 0.1f), 1.2f * u)
        // Las arrugas del cartón y la cinta vieja.
        drawLine(s(0.2f), Offset(fr.left + w * 0.2f, fr.top + h * 0.12f), Offset(fr.left + w * 0.24f, fr.bottom), 0.8f * u)
        drawRect(c(Mat.CINTA, fr.center).copy(alpha = if (tinta) 1f else 0.7f), Offset(fr.right - w * 0.1f, fr.top), Size(w * 0.05f, fr.height))
        // La etiqueta escrita a mano, pegada con cinta, un poco torcida.
        val rr = rotuloEn(w, h)
        rotate(-1.2f, rr.center) {
            drawRect(s(0.3f), Offset(rr.left + 1.5f * u, rr.top + 2f * u), rr.size)
            drawRect(papelTarjeta(rr.center), rr.topLeft, rr.size)
            linea(caja(rr), rr.center, 0.8f)
            val t = Rect(rr.center.x - w * 0.06f, rr.top - h * 0.025f, rr.center.x + w * 0.06f, rr.top + h * 0.025f)
            drawRect(c(Mat.CINTA, t.center).copy(alpha = if (tinta) 1f else 0.85f), t.topLeft, t.size)
            if (tinta) marco(t, if (o) CREMA.copy(alpha = 0.3f) else GRABAR, 0.7f * u)
        }
    }

    override val rotulo = Rect(0.07f, 0.55f, 0.93f, 0.87f)
    override fun tintaRotulo(oscuro: Boolean) = if (EstadoTema.cara == Cara.INTENSA) TrazosTinta.GRABAR else TINTA
    override val letraRotulo: FontFamily = LetraExtra.franklinRotulo
    override val salida = SALIDA
    override val placa: Rect = Rect(0.36f, 0.06f, 0.64f, 0.3f)
    override fun tintaPlaca(oscuro: Boolean) = tintaRotulo(oscuro)

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios, de la versión 1)

    override fun DrawScope.interior(oscuro: Boolean) = with(IdeasPsicodeliaV1) { interior(oscuro) }
    override fun tintaInterior(oscuro: Boolean) = IdeasPsicodeliaV1.tintaInterior(oscuro)
    override val pared: Dp = 20.dp
    override val columnas = 2
    override val lineas = 4
    override fun forma(semilla: Int): Shape = IdeasPsicodeliaV1.forma(semilla)
    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) = with(IdeasPsicodeliaV1) { adornoFicha(p, grande, semilla) }
    override val margenes = MargenesFicha(17.dp, 15.dp, 16.dp, 26.dp, 26.dp, 30.dp)
    override val proporcionFicha = 0.94f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.8f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false
    override fun DrawScope.marcaPostura(id: String, p: Pagina) = with(IdeasPsicodeliaV1) { marcaPostura(id, p) }
    override fun DrawScope.marcaFijada(p: Pagina) = with(IdeasPsicodeliaV1) { marcaFijada(p) }
    override val desorden = 2.5f
}

/**
 * Lo que queda de la versión 1 del Ideario de Psicodelia (la caja de sencillos vista desde arriba, las fundas de papel de las
 * fichas y sus marcas), copiado tal cual con sus colores y ayudantes: lo rehace, si hace falta, otra tanda.
 */
private object IdeasPsicodeliaV1 {
    // ---------------------------------------------------------------- la paleta de la tienda
    private val MAGENTA = TrazosPsicodelia.MAGENTA
    private val VERDE = TrazosPsicodelia.VERDE
    private val NARANJA = TrazosPsicodelia.NARANJA
    private val AZUL = TrazosPsicodelia.AZUL
    private val AMARILLO = TrazosPsicodelia.AMARILLO
    private val VIOLETA = TrazosPsicodelia.VIOLETA
    /** La luz negra: lo que brilla sobre el negro violeta. */
    private val LIMON = TrazosPsicodelia.LIMON
    private val FMAGENTA = TrazosPsicodelia.FLUOR_MAGENTA
    private val FVIOLETA = TrazosPsicodelia.FLUOR_VIOLETA
    private val FNARANJA = TrazosPsicodelia.FLUOR_NARANJA
    private val FAZUL = Color(0xFF6CF0C8)
    private val SALA = TrazosPsicodelia.SALA
    private val VINILO = TrazosPsicodelia.VINILO
    /** La tinta de las figuras: un violeta casi negro. */
    private val TINTA = TrazosPsicodelia.TINTA
    /** La pared de la tienda, pintada de crema, y su sombra. */
    private val PARED = Color(0xFFF1E6D3)
    private val PARED_SOMBRA = Color(0xFFCDBDA6)
    /** El mostrador, pintado a mano de violeta: su luz y su sombra. */
    private val MOSTRADOR = Color(0xFF4E2D80)
    private val MOSTRADOR_LUZ = Color(0xFF6C4AA6)
    private val MOSTRADOR_OSC = Color(0xFF2C1752)
    /** El pino de las cajas y de la cubierta, y sus nudos. */
    private val PINO = Color(0xFFCFA673)
    private val PINO_LUZ = Color(0xFFE8CB98)
    private val PINO_OSC = Color(0xFF8C6A42)
    private val NUDO = Color(0xFF7A532E)
    /** La cubierta del mostrador, de pino barnizado, más oscura. */
    private val CUBIERTA = Color(0xFFB98652)
    /** La duela del piso. */
    private val DUELA = Color(0xFF9A6B42)
    /** El papel de las fundas: el del periódico, el blanco, el cartón y la estraza. */
    private val PAPEL = TrazosPsicodelia.PAPEL
    private val BLANCO = Color(0xFFFBF6EC)
    private val CARTON = Color(0xFFC9A877)
    /** La tarjeta del rótulo: cartulina crema; bajo la luz negra, el papel que se enciende de lila. */
    private val CARTULINA = Color(0xFFF7EEDC)
    private val CARTULINA_NOCHE = Color(0xFFD2C4F2)
    /** Las casas pintadas que se ven por la ventana. */
    private val CASA_GRIS = TrazosPsicodelia.CASA_GRIS
    private val CASA_BLANCA = TrazosPsicodelia.CASA_BLANCA
    private val CASA_ROSA = TrazosPsicodelia.CASA_ROSA
    private val CASA_TURQUESA = TrazosPsicodelia.CASA_TURQUESA
    private val CASA_AMARILLA = TrazosPsicodelia.CASA_AMARILLA
    private val NIEBLA = TrazosPsicodelia.NIEBLA
    /** El metal: el cromo de la caja registradora y del tocadiscos. */
    private val CROMO = Color(0xFFC9CDD0)
    private val CROMO_OSC = Color(0xFF7D8388)
    /** El oro del disco de oro. */
    private val ORO = Color(0xFFE2B33C)
    private val ORO_OSC = Color(0xFF9A7419)

    /** Los pares que vibran: un color y su complementario, como en los carteles. */
    private val PARES = listOf(MAGENTA to VERDE, NARANJA to AZUL, AMARILLO to VIOLETA, VERDE to MAGENTA, AZUL to NARANJA, VIOLETA to AMARILLO)

    /** El color de lo que no brilla: de día, tal cual; de noche, hundido en el negro violeta de la luz negra. */
    private fun t(c: Color, oscuro: Boolean, k: Float = 0.8f): Color = if (oscuro) mezcla(c, SALA, k) else c

    /** Lo que se pintó o se imprimió en tinta fluorescente: de día, su color; bajo la luz negra, el que brilla. */
    private fun fluor(c: Color, oscuro: Boolean): Color = if (!oscuro) c else when (c) {
        MAGENTA -> FMAGENTA
        VERDE -> LIMON
        NARANJA -> FNARANJA
        AMARILLO -> LIMON
        AZUL -> FAZUL
        VIOLETA -> FVIOLETA
        else -> mezcla(c, FVIOLETA, 0.45f)
    }

    /** La sombra con color: de día, el violeta hondo de los muebles; de noche, el negro violeta de la sala. */
    private fun sombra(oscuro: Boolean, a: Float) = if (oscuro) SALA.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el lila apagado del tubo de luz negra. */
    private fun brillo(oscuro: Boolean, a: Float) = if (oscuro) FVIOLETA.copy(alpha = (a * 0.35f).coerceIn(0f, 1f)) else luzMueble(a)

    /** El halo de algo que brilla (la luz negra, la lámpara de cera, la tinta fluorescente). */
    private fun DrawScope.resplandor(c: Offset, r: Float, col: Color, fuerza: Float = 1f) {
        mancha(c, r, col.copy(alpha = (0.18f * fuerza).coerceIn(0f, 1f)))
        mancha(c, r * 0.45f, col.copy(alpha = (0.22f * fuerza).coerceIn(0f, 1f)))
    }

    /** Una forma que brilla bajo la luz negra: el halo de su color alrededor y la forma encima. */
    private fun DrawScope.brilla(p: Path, col: Color, g: Float) {
        drawPath(p, col.copy(alpha = 0.1f * col.alpha), style = Stroke(g * 3.2f, join = StrokeJoin.Round))
        drawPath(p, col.copy(alpha = 0.22f * col.alpha), style = Stroke(g * 1.4f, join = StrokeJoin.Round))
        drawPath(p, col)
    }

    // ---------------------------------------------------------------- ayudantes de la tienda

    /** Una tabla de pino en [r], con su veta, algún nudo y el canto de arriba a la luz. */
    private fun DrawScope.pino(r: Rect, base: Color, semilla: Int, oscuro: Boolean, vertical: Boolean = false, nudos: Int = 1) {
        val u = density
        drawRect(base, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            vetas(r, t(NUDO, oscuro, 0.7f).copy(alpha = if (oscuro) 0.4f else 0.26f), semilla, densidad = 0.5f, vertical = vertical, grosor = 0.7f, ondulacion = 0.7f)
            for (k in 0 until nudos) {
                if (azar(semilla, k + 50) < 0.35f) continue
                val p = Offset(r.left + r.width * (0.15f + 0.7f * azar(semilla, k + 51)), r.top + r.height * (0.25f + 0.5f * azar(semilla, k + 52)))
                val a = minOf(r.width, r.height) * 0.16f
                val tam = if (vertical) Size(a * 0.8f, a * 1.6f) else Size(a * 1.6f, a * 0.8f)
                drawOval(t(NUDO, oscuro, 0.7f).copy(alpha = 0.55f), Offset(p.x - tam.width / 2, p.y - tam.height / 2), tam)
                drawOval(t(NUDO, oscuro, 0.7f).copy(alpha = 0.3f), Offset(p.x - tam.width, p.y - tam.height), Size(tam.width * 2, tam.height * 2), style = Stroke(0.6f * u))
            }
        }
    }

    /**
     * Una funda de sencillo vista de frente en [r] (sin girar), con [estilo]: 0, papel del periódico; 1, papel blanco; 2,
     * cartón; 3, la funda impresa de un color con su franja; 4, la funda ilustrada. Con [agujero], se ve el agujero redondo
     * con la etiqueta de [etiqueta] (para el sencillo que se sacó a medias).
     */
    private fun DrawScope.funda(r: Rect, estilo: Int, color: Color, oscuro: Boolean, agujero: Float = 0f, etiqueta: Color = NARANJA, semilla: Int = 0) {
        val u = density
        val base = when (estilo) { 0 -> PAPEL; 1 -> BLANCO; 2 -> CARTON; 3 -> mezcla(color, Color.White, 0.35f); else -> mezcla(color, Color.White, 0.15f) }
        val col = if (oscuro) (if (estilo <= 1) mezcla(base, SALA, 0.68f) else mezcla(fluor(color, true), SALA, 0.72f)) else base
        drawRect(col, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            when (estilo) {
                3 -> drawRect(fluor(color, oscuro).let { if (oscuro) mezcla(it, SALA, 0.4f) else mezcla(it, Color.Black, 0.12f) }, Offset(r.left, r.top + r.height * 0.1f), Size(r.width, minOf(r.height * 0.12f, 3f * u)))
                4 -> {
                    val c2 = fluor(TrazosPsicodelia.complementario(color), oscuro).let { if (oscuro) mezcla(it, SALA, 0.35f) else it }
                    for (k in 0 until 3) drawPath(TrazosPsicodelia.onda(r.left, r.right, r.top + 3f * u + k * 3.2f * u, 1f * u, r.width * 0.3f, k * 0.2f), c2, style = Stroke(1.2f * u))
                }
            }
            // La muesca del pulgar, arriba, por donde asoma el canto del disco.
            if (estilo <= 2 && r.width > 12f * u) {
                val cm = Offset(r.center.x + r.width * (azar(semilla, 7) - 0.5f) * 0.5f, r.top)
                val rm = minOf(r.width * 0.09f, 5f * u)
                drawCircle(VINILO, rm, cm)
                drawArc(Color.White.copy(alpha = 0.15f), 20f, 140f, false, Offset(cm.x - rm * 0.7f, cm.y - rm * 0.7f), Size(rm * 1.4f, rm * 1.4f), style = Stroke(0.4f * u))
            }
            if (agujero > 0f) {
                val ca = Offset(r.center.x, r.top + agujero)
                val ra = r.width * 0.3f
                drawCircle(VINILO, ra, ca)
                val e = fluor(etiqueta, oscuro)
                if (oscuro) drawCircle(e.copy(alpha = 0.35f), ra * 0.85f, ca)
                drawCircle(e, ra * 0.72f, ca)
                drawCircle(mezcla(e, Color.White, 0.35f), ra * 0.58f, ca, style = Stroke(0.5f * u))
                drawCircle(col, ra * 0.26f, ca)
                drawArc(sombra(oscuro, 0.4f), 180f, 150f, false, Offset(ca.x - ra, ca.y - ra), Size(ra * 2, ra * 2), style = Stroke(1.2f * u))
            }
        }
        // El canto de arriba, a la luz; de noche, el papel blanco se enciende un poco con la luz negra.
        drawLine(if (oscuro) FVIOLETA.copy(alpha = if (estilo <= 1) 0.55f else 0.25f) else luzMueble(0.75f), Offset(r.left, r.top + 0.4f * u), Offset(r.right, r.top + 0.4f * u), 0.8f * u)
        drawLine(sombra(oscuro, 0.25f), Offset(r.left, r.top), Offset(r.left, r.bottom), 0.5f * u)
    }

    // ---------------------------------------------------------------- el interior: la caja vista desde arriba

    fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val pl = 18f * u; val pa = 30f * u; val pf = 24f * u
        val fondo = Rect(pl, pa, w - pl, h - pf)
        // El fondo de la caja, en sombra.
        drawRect(t(mezcla(PINO, PINO_OSC, 0.6f), oscuro, 0.82f), fondo.topLeft, fondo.size)
        // Los sencillos de canto, del fondo (arriba) hacia el frente (abajo): de cada uno se ve el canto de la funda y un poco
        // de su cara; cada tanto, el separador de un tema con su pestaña, y un hueco donde falta uno.
        val colores = listOf(MAGENTA, NARANJA, AMARILLO, VERDE, AZUL, VIOLETA)
        var y = fondo.top + 6f * u
        var k = 0
        while (y < fondo.bottom) {
            val alto = (8f + 12f * azar(801, k)) * u
            val giro = (azar(802, k) - 0.5f) * 1.6f
            val dxs = (azar(803, k) - 0.5f) * 6f * u
            val r = Rect(fondo.left + 3f * u + dxs, y, fondo.right - 3f * u + dxs, y + alto + 8f * u)
            val hueco = k % 23 == 11
            val divisor = k % 14 == 6
            rotate(giro, r.center) {
                if (hueco) {
                    drawRect(sombra(oscuro, 0.5f), r.topLeft, Size(r.width, alto))
                } else if (divisor) {
                    val par = PARES[Math.floorMod(k / 14, PARES.size)]
                    val col = if (oscuro) fluor(par.first, true) else mezcla(par.first, PAPEL, 0.45f)
                    val izq = k % 28 == 6
                    val pw = r.width * 0.24f
                    val px = if (izq) r.left + r.width * 0.08f else r.right - r.width * 0.08f - pw
                    val tab = Path().apply {
                        addRect(Rect(r.left, r.top, r.right, r.bottom))
                        addRoundRect(RoundRect(Rect(px, r.top - 9f * u, px + pw, r.top + 2f * u), CornerRadius(3f * u)))
                    }
                    drawPath(tab, sombra(oscuro, 0.3f), style = Stroke(2.4f * u))
                    if (oscuro) drawRoundRect(col.copy(alpha = 0.22f), Offset(px - 3f * u, r.top - 12f * u), Size(pw + 6f * u, 14f * u), CornerRadius(5f * u))
                    drawRect(if (oscuro) mezcla(col, SALA, 0.55f) else col, r.topLeft, r.size)
                    drawRoundRect(col, Offset(px, r.top - 9f * u), Size(pw, 11f * u), CornerRadius(3f * u))
                    with(TrazosPsicodelia) { margarita(Offset(px + pw / 2, r.top - 3.5f * u), 3.4f * u, fluor(par.second, oscuro), fluor(AMARILLO, oscuro), 6) }
                } else {
                    val a = azar(804, k)
                    val estilo = when { a < 0.44f -> 0; a < 0.62f -> 1; a < 0.8f -> 2; a < 0.92f -> 3; else -> 4 }
                    funda(r, estilo, mezcla(colores[Math.floorMod(k * 5, colores.size)], PAPEL, 0.45f), oscuro, semilla = 811 + k)
                }
                // La sombra que le echa la funda de adelante, abajo.
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombra(oscuro, 0.35f)), r.top + alto - 4f * u, r.top + alto), Offset(r.left, r.top + alto - 4f * u), Size(r.width, 4f * u))
            }
            y += alto; k++
        }
        // De noche, la luz negra cae desde arriba y deja hundidas las esquinas.
        if (oscuro) {
            mancha(Offset(w * 0.6f, h * 0.2f), maxOf(w, h) * 0.7f, FVIOLETA.copy(alpha = 0.08f))
            drawRect(Brush.radialGradient(listOf(Color.Transparent, SALA.copy(alpha = 0.55f)), Offset(w * 0.55f, h * 0.35f), maxOf(w, h) * 0.75f))
        }
        // Las paredes de la caja vistas desde arriba: la de atrás y la de la izquierda en sombra, las otras a la luz.
        val atras = Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(w - pl, pa); lineTo(pl, pa); close() }
        val izq = Path().apply { moveTo(0f, 0f); lineTo(pl, pa); lineTo(pl, h - pf); lineTo(0f, h); close() }
        val der = Path().apply { moveTo(w, 0f); lineTo(w, h); lineTo(w - pl, h - pf); lineTo(w - pl, pa); close() }
        val delante = Path().apply { moveTo(0f, h); lineTo(pl, h - pf); lineTo(w - pl, h - pf); lineTo(w, h); close() }
        listOf(Triple(atras, Rect(0f, 0f, w, pa), mezcla(PINO_LUZ, Color.Black, 0.12f)), Triple(izq, Rect(0f, 0f, pl, h), mezcla(PINO, Color.Black, 0.22f)),
            Triple(der, Rect(w - pl, 0f, w, h), PINO), Triple(delante, Rect(0f, h - pf, w, h), PINO_LUZ)).forEachIndexed { i, (p, rr, col) ->
            clipPath(p) { pino(rr, t(col, oscuro, 0.78f), 850 + i, oscuro, vertical = i == 1 || i == 2, nudos = 2) }
            drawPath(p, sombra(oscuro, 0.3f), style = Stroke(0.6f * u))
        }
        sombraInterior(fondo, 20f * u, if (oscuro) 0.45f else 0.28f)
        drawRect(brillo(oscuro, 0.5f), Offset.Zero, Size(w, 1.2f * u))
        // Los clavos de las esquinas.
        for ((x, yy) in listOf(pl * 0.5f to pa * 0.4f, w - pl * 0.5f to pa * 0.4f, pl * 0.5f to h - pf * 0.4f, w - pl * 0.5f to h - pf * 0.4f)) {
            drawCircle(t(Color(0xFF4A4440), oscuro, 0.6f), 1.3f * u, Offset(x, yy))
            drawCircle(brillo(oscuro, 0.5f), 0.5f * u, Offset(x - 0.4f * u, yy - 0.4f * u))
        }
        // Una margarita pintada a mano en la tabla de delante.
        val cm = Offset(w * 0.5f, h - pf * 0.5f)
        if (oscuro) resplandor(cm, pf * 1.2f, LIMON, 0.5f)
        with(TrazosPsicodelia) { margarita(cm, pf * 0.32f, fluor(AMARILLO, oscuro), fluor(NARANJA, oscuro), 8) }
        granoMueble(Rect(0f, 0f, w, h), oscuro, 0.7f)
    }

    fun tintaInterior(oscuro: Boolean) = if (oscuro) Color(0xFFF2EAF7) else TINTA
    val pared: Dp = 20.dp
    val columnas = 2
    val lineas = 4

    // ---------------------------------------------------------------- las fichas: fundas de papel de sencillo

    fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        val e = minOf(s.width, s.height) * 0.012f
        addRoundRect(RoundRect(Rect(0f, 0f, s.width, s.height), CornerRadius(e)))
    }

    fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.4f else 1f
        val m = minOf(w, h)
        // La solapa pegada por detrás, que se transparenta a la izquierda y abajo.
        drawRect(sombraMueble(0.045f), Offset.Zero, Size(w * 0.05f, h))
        drawRect(sombraMueble(0.04f), Offset(0f, h - h * 0.045f), Size(w, h * 0.045f))
        drawLine(p.tenue.copy(alpha = 0.16f), Offset(w * 0.05f, 0f), Offset(w * 0.05f, h), 0.6f * u)
        // La marca del aro del disco, que se fue marcando en el papel.
        val cd = Offset(w * 0.5f, h * 0.54f)
        drawCircle(sombraMueble(0.05f), m * 0.44f, cd, style = Stroke(3f * u * g))
        drawCircle(luzMueble(0.35f), m * 0.44f - 2f * u * g, cd, style = Stroke(1f * u * g))
        // El agujero redondo de la funda, con la etiqueta del disco y su agujero grande, tenues para que se lea encima.
        val ra = m * (if (grande) 0.17f else 0.2f)
        val et = PARES[Math.floorMod(semilla, PARES.size)]
        drawCircle(p.tinta.copy(alpha = 0.07f), ra, cd)
        drawCircle(et.first.copy(alpha = 0.18f), ra * 0.8f, cd)
        drawCircle(et.second.copy(alpha = 0.16f), ra * 0.62f, cd, style = Stroke(0.8f * u * g))
        drawCircle(p.color, ra * 0.28f, cd)
        drawCircle(p.tinta.copy(alpha = 0.12f), ra * 0.28f, cd, style = Stroke(0.6f * u))
        // El filo del corte: la sombra del papel arriba a la izquierda y la luz abajo a la derecha.
        drawArc(sombraMueble(0.22f), 160f, 160f, false, Offset(cd.x - ra, cd.y - ra), Size(ra * 2, ra * 2), style = Stroke(1.1f * u * g))
        drawArc(luzMueble(0.7f), -20f, 160f, false, Offset(cd.x - ra, cd.y - ra), Size(ra * 2, ra * 2), style = Stroke(0.8f * u * g))
        // La muesca del pulgar, arriba, por donde asoma el canto negro del disco.
        val rm = m * 0.07f
        val cm = Offset(w * 0.5f, 0f)
        drawCircle(VINILO, rm, cm)
        for (k in 1..3) drawArc(Color.White.copy(alpha = 0.1f), 20f, 140f, false, Offset(cm.x - rm * (1f - 0.22f * k), cm.y - rm * (1f - 0.22f * k)), Size(rm * 2 * (1f - 0.22f * k), rm * 2 * (1f - 0.22f * k)), style = Stroke(0.4f * u))
        drawArc(luzMueble(0.8f), 0f, 180f, false, Offset(cm.x - rm, cm.y - rm), Size(rm * 2, rm * 2), style = Stroke(0.8f * u))
        // La onda de arcoíris que imprime la tienda en sus fundas, abajo.
        val yo = h - 7f * u * g
        drawPath(TrazosPsicodelia.onda(w * 0.1f, w * 0.9f, yo, 1.4f * u * g, 14f * u * g), TrazosPsicodelia.arcoiris(Offset(w * 0.1f, yo), Offset(w * 0.9f, yo), 0.55f), style = Stroke(1.3f * u * g, cap = StrokeCap.Round))
        // En la grande, el sello de goma de la tienda: una margarita en su aro de ondas, magenta, abajo a la izquierda.
        if (grande) {
            val cs = Offset(30f * u, h - 34f * u)
            val rs = 15f * u
            rotate(-14f, cs) {
                val tinta = MAGENTA.copy(alpha = 0.32f)
                drawCircle(tinta, rs, cs, style = Stroke(1.3f * u))
                drawCircle(tinta, rs * 0.78f, cs, style = Stroke(0.6f * u))
                for (k in 0 until 18) {
                    val a = 2f * PI.toFloat() * k / 18
                    drawCircle(tinta, 0.7f * u, Offset(cs.x + rs * 0.89f * cos(a), cs.y + rs * 0.89f * sin(a)))
                }
                with(TrazosPsicodelia) { margarita(cs, rs * 0.55f, tinta, null, 8) }
                drawCircle(tinta, rs * 0.13f, cs)
            }
        }
    }

    fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El disco de oro: ya está hecho.
            "hecho" -> {
                drawCircle(Brush.radialGradient(listOf(mezcla(ORO, Color.White, 0.45f), ORO, ORO_OSC), c - Offset(s * 0.15f, s * 0.15f), s * 0.6f), s * 0.46f, c)
                drawCircle(ORO_OSC, s * 0.36f, c, style = Stroke(s * 0.035f))
                drawCircle(MAGENTA, s * 0.18f, c)
                drawCircle(mezcla(ORO, Color.White, 0.5f), s * 0.06f, c)
                drawCircle(ORO_OSC, s * 0.46f, c, style = Stroke(s * 0.05f))
            }
            // La flor que vibra: convence.
            "convence" -> with(TrazosPsicodelia) { flor(c, s * 0.46f, MAGENTA, VERDE, 6, 0.6f) }
            // El disco que gira y sus dos estelas: le sigue dando vueltas.
            "piensa" -> {
                drawCircle(VINILO, s * 0.3f, c)
                drawCircle(p.acento, s * 0.12f, c)
                drawCircle(p.color, s * 0.04f, c)
                drawArc(p.acento, -150f, 80f, false, Offset(c.x - s * 0.44f, c.y - s * 0.44f), Size(s * 0.88f, s * 0.88f), style = Stroke(s * 0.08f, cap = StrokeCap.Round))
                drawArc(p.acento, 30f, 80f, false, Offset(c.x - s * 0.44f, c.y - s * 0.44f), Size(s * 0.88f, s * 0.88f), style = Stroke(s * 0.08f, cap = StrokeCap.Round))
            }
            // Dos burbujas de aceite que flotan: todavía no se asienta.
            "duda" -> {
                drawCircle(p.tenue, s * 0.27f, Offset(c.x - s * 0.1f, c.y + s * 0.1f), style = Stroke(s * 0.08f))
                drawCircle(p.tenue, s * 0.13f, Offset(c.x + s * 0.27f, c.y - s * 0.25f), style = Stroke(s * 0.07f))
                drawArc(p.tenue, 200f, 60f, false, Offset(c.x - s * 0.28f, c.y - s * 0.08f), Size(s * 0.36f, s * 0.36f), style = Stroke(s * 0.06f, cap = StrokeCap.Round))
            }
            // El disco partido: así no.
            "desacuerdo" -> {
                val r = s * 0.42f
                val disco = Path().apply { moveTo(c.x, c.y); arcTo(Rect(c, r), -40f, 290f, false); close() }
                drawPath(disco, VINILO)
                drawCircle(MAGENTA, r * 0.38f, c)
                drawCircle(p.color, r * 0.1f, c)
                val pedazo = Path().apply { val o = c + Offset(s * 0.1f, -s * 0.08f); moveTo(o.x, o.y); arcTo(Rect(o, r), -95f, 45f, false); close() }
                drawPath(pedazo, VINILO)
            }
            else -> {}
        }
    }

    /** Una chincheta de plástico en forma de margarita, clavada de lado: así se clavan las tarjetas de las cajas. */
    fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center + Offset(s * 0.06f, -s * 0.06f)
        // La aguja, que entra en diagonal, y la sombra de la cabeza.
        drawLine(Color(0xFF8A8C88), c, c + Offset(-s * 0.4f, s * 0.4f), s * 0.08f, StrokeCap.Round)
        drawCircle(sombraMueble(0.25f), s * 0.34f, c + Offset(s * 0.05f, s * 0.07f))
        with(TrazosPsicodelia) { margarita(c, s * 0.4f, MAGENTA, AMARILLO, 6, 0.3f) }
        drawCircle(Color.White.copy(alpha = 0.55f), s * 0.07f, c + Offset(-s * 0.15f, -s * 0.17f))
    }
}
