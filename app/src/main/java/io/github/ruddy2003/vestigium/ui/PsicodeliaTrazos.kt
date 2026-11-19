package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/*
 * Los colores y los trazos de dibujo de Psicodelia, versión 2 (`docs/diseno/temas/psicodelia.md`, «Los colores de
 * dibujo»; resumen para las tandas en `docs/diseno/temas/psicodelia_base.md`). Uno por cara:
 * - [TrazosCiudad]: la ciudad de fines de los sesenta vista como una foto de revista de los 70. Pocos colores, apagados;
 *   luz de la izquierda con sombras café; grano de película y viñeta.
 * - [TrazosTinta]: el cartel de la semana, a tres tintas planas (magenta, bermellón y ultramar) sobre papel crema o sobre
 *   negro de terciopelo, que se calientan con la intensidad ([EstadoTema.intensidad]).
 * Reemplazan a los trazos de la versión 1, retirados en el cierre de la versión 2.
 *
 * Reglas de todos los trazos de aquí: ningún bucle con paso que pueda valer 0 (los pasos se acotan a 0.5 px o más) y
 * ninguna coordenada no finita (los tamaños vacíos o negativos no dibujan nada).
 */

/**
 * La ciudad (cara BASE): sus colores con nombre, todos apagados, y sus trazos básicos. Color de película vieja: los negros
 * se levantan hacia el café, las luces se van al crema, los rojos se apagan hacia el ladrillo. Nada brilla.
 */
object TrazosCiudad {
    // ------------------------------------------------------------------------------------------------ colores

    /** El cielo de la tarde: de la bruma, arriba, a la luz baja, abajo; y el sol velado por la niebla. */
    val BRUMA = Color(0xFFD9CDB6)
    val LUZ_BAJA = Color(0xFFEDBE8C)
    val SOL_VELADO = Color(0xFFF6DDB4)

    /** Las victorianas: casi todas grises cálidas; una pintada, verde azul desteñido por la foto. */
    val CASA_GRIS = Color(0xFFA0968A)
    val CASA_GRIS_OSCURA = Color(0xFF958B7E)
    val CASA_GRIS_CLARA = Color(0xFFA89C8C)
    val CASA_PINTADA = Color(0xFF7E958E)
    /** Las fachadas en el orden de la calle de la lámina: tres grises, la pintada y vuelta a empezar. */
    val CASAS = listOf(CASA_GRIS, CASA_GRIS_OSCURA, CASA_GRIS_CLARA, CASA_PINTADA)

    /** Hacia dónde va lo que mira al sol de la tarde (a la izquierda) y lo que queda en sombra (sombra café, nunca gris). */
    val AL_SOL = Color(0xFFF4E6CC)
    val SOMBRA = Color(0xFF4A3528)

    /** Los postes de madera y los cables del trolebús. */
    val POSTE = Color(0xFF3E3128)
    /** El pasamanos del autobús, con su brillo. */
    val PASAMANOS = Color(0xFF8C8270)
    val PASAMANOS_BRILLO = Color(0xFFD8CDB5)
    /** El coche aparcado en sombra: el rojo apagado hacia el ladrillo (también el del ladrillo de [ladrillos]). */
    val COCHE = Color(0xFF7A3424)

    /** El cuarto de noche: la pared a media luz, la pantalla plisada encendida y su luz, la madera y la etiqueta del disco. */
    val PARED_NOCHE = Color(0xFF1F1712)
    val LAMPARA = Color(0xFFF2C996)
    val LAMPARA_LUZ = Color(0xFFF0A35A)
    val MADERA = Color(0xFF6E4630)
    val ETIQUETA = Color(0xFFC8662A)

    /** El rojo de letrero (el error de la ciudad clara), para el filete de [placaLetrero]. */
    val LETRERO = Color(0xFFA01E2C)

    /** La viñeta de la foto se va hacia este café. */
    val VINETA = Color(0xFF3A2410)

    /** La fuerza del grano de película (de la lámina): de día y de noche. */
    const val GRANO_DIA = 0.22f
    const val GRANO_NOCHE = 0.30f
    /** La fuerza de la viñeta (de la lámina): de día y de noche. */
    const val VINETA_DIA = 0.28f
    const val VINETA_NOCHE = 0.35f

    // ------------------------------------------------------------------------------------------------ color de foto

    /** Lo que mira al sol de la tarde: [c] aclarado hacia [AL_SOL] (0.35, como la lámina). */
    fun alSol(c: Color, k: Float = 0.35f): Color = lerp(c, AL_SOL, k.coerceIn(0f, 1f))

    /** Lo que queda en sombra: [c] llevado hacia el café [SOMBRA] (0.42, como la lámina). */
    fun enSombra(c: Color, k: Float = 0.42f): Color = lerp(c, SOMBRA, k.coerceIn(0f, 1f))

    /**
     * Cualquier color pasado por la película vieja **[Diseño propio]**: los oscuros se levantan hacia el café (hasta un
     * 18 %), las luces se van al crema (hasta un 12 %) y todo pierde un 10 % de saturación hacia su propio gris cálido.
     * Sirve para que un color que no está en la lista no desentone; los de la lista ya están pasados.
     */
    fun pelicula(c: Color): Color {
        val l = c.luminance()
        val gris = lerp(Color(l, l, l, c.alpha), SOMBRA.copy(alpha = c.alpha), 0.15f)
        val apagado = lerp(c, gris, 0.1f)
        return lerp(lerp(apagado, SOMBRA.copy(alpha = c.alpha), 0.18f * (1f - l)), AL_SOL.copy(alpha = c.alpha), 0.12f * l)
    }

    // ------------------------------------------------------------------------------------------------ película

    /**
     * El grano de película: puntos claros (crema) y oscuros (café) repartidos al azar, pero siempre igual para la misma
     * [semilla], en celdas de [celda] dp. [fuerza]: [GRANO_DIA] o [GRANO_NOCHE] (la de la lámina). Cada punto es un
     * cuadrito que llena su celda.
     *
     * **Cómo se dibuja (19 nov 2026):** el grano de una zona se pinta una sola vez en una imagen chica, de un píxel por
     * celda ([imagenGrano], guardada en caché por tamaño, semilla y fuerza), y esa imagen se pega estirada a la zona sin
     * suavizar (`FilterQuality.None`): cada píxel vuelve a ser el cuadrito de su celda, en el mismo lugar y del mismo color.
     * Antes se dibujaba punto por punto: en Android, `drawPoints` de Compose hace una operación por punto, así que el
     * estante del diario llevaba unas 90 000 en el arnés (y varias veces más con medio año de entradas) y cada mueble de
     * Vida unas 27 000, que el teléfono volvía a pintar en cada cuadro mientras se movía el fondo vivo. Ahora es una sola.
     * Lo único que cambia es el borde de cada cuadrito cuando cae entre dos píxeles: antes se suavizaba, ahora va entero
     * a un lado (con la fuerza del grano, menos de un 2 % de diferencia en esos píxeles).
     */
    fun DrawScope.grano(fuerza: Float, semilla: Int = 1, celda: Float = 1.4f, zona: Rect = Rect(Offset.Zero, size)) {
        if (fuerza <= 0f || zona.width <= 0f || zona.height <= 0f || !zona.width.isFinite() || !zona.height.isFinite()) return
        val paso = max(celda * density, 1f)
        // Cuántas columnas y filas: las mismas cuentas (con el mismo paso sumado) que el dibujo punto por punto.
        var cols = 0
        var x = zona.left + paso / 2
        while (x < zona.right) { cols++; x += paso }
        var filas = 0
        var y = zona.top + paso / 2
        while (y < zona.bottom) { filas++; y += paso }
        if (cols <= 0 || filas <= 0) return
        val img = imagenGrano(cols, filas, semilla, fuerza)
        withTransform({ translate(zona.left, zona.top); scale(paso, paso, Offset.Zero) }) {
            drawImage(img, srcOffset = IntOffset.Zero, srcSize = IntSize(cols, filas), dstOffset = IntOffset.Zero, dstSize = IntSize(cols, filas),
                filterQuality = FilterQuality.None)
        }
    }

    /** Las imágenes del grano ya pintadas, de la más vieja a la más nueva, con un tope de píxeles (uno por celda). */
    private val granos = LinkedHashMap<List<Any>, ImageBitmap>(8, 0.75f, true)
    private var pixelesGrano = 0L
    private const val TOPE_GRANO = 3_000_000L

    /**
     * El grano de [cols] × [filas] celdas con [semilla] y [fuerza], un píxel por celda: transparente o del claro o el oscuro
     * con su opacidad (las cuatro tandas de siempre: débil y fuerte de cada uno). Se pinta una vez y se guarda.
     */
    private fun imagenGrano(cols: Int, filas: Int, semilla: Int, fuerza: Float): ImageBitmap = synchronized(granos) {
        val clave = listOf<Any>(cols, filas, semilla, fuerza)
        granos[clave]?.let { return it }
        val img = ImageBitmap(cols, filas)
        val tandas = List(4) { ArrayList<Offset>() }
        for (f in 0 until filas) for (c in 0 until cols) {
            val v = azarFijo(c, f, semilla) * 2f - 1f
            if (abs(v) > 0.35f) tandas[(if (v > 0) 0 else 2) + (if (abs(v) > 0.75f) 1 else 0)].add(Offset(c + 0.5f, f + 0.5f))
        }
        val alfas = floatArrayOf(0.22f, 0.45f, 0.22f, 0.45f)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(cols.toFloat(), filas.toFloat())) {
            tandas.forEachIndexed { i, puntos ->
                if (puntos.isNotEmpty()) drawPoints(puntos, PointMode.Points, (if (i < 2) AL_SOL else SOMBRA).copy(alpha = (alfas[i] * fuerza).coerceIn(0f, 1f)),
                    strokeWidth = 1f, cap = StrokeCap.Square)
            }
        }
        granos[clave] = img; pixelesGrano += cols.toLong() * filas
        val it = granos.entries.iterator()
        while (pixelesGrano > TOPE_GRANO && it.hasNext()) {
            val e = it.next()
            if (e.key == clave) continue
            pixelesGrano -= e.value.width.toLong() * e.value.height; it.remove()
        }
        img
    }

    /** La viñeta de la foto: el centro limpio y los bordes hacia [color], con [fuerza] ([VINETA_DIA] o [VINETA_NOCHE]). */
    fun DrawScope.vineta(fuerza: Float, color: Color = VINETA) {
        if (fuerza <= 0f || size.minDimension <= 0f) return
        val r = sqrt(size.width * size.width + size.height * size.height) / 2f
        drawRect(Brush.radialGradient(0f to color.copy(alpha = 0f), 0.62f to color.copy(alpha = 0f), 1f to color.copy(alpha = fuerza.coerceIn(0f, 1f)),
            center = center, radius = r))
    }

    // ------------------------------------------------------------------------------------------------ la calle

    /**
     * Los dos cables paralelos del trolebús, de [desde] a [hasta], que cuelgan [comba] en el medio, separados [separacion]
     * (en vertical). [grosor] en px.
     */
    fun DrawScope.cables(desde: Offset, hasta: Offset, comba: Float, separacion: Float, grosor: Float, color: Color = POSTE) {
        if (grosor <= 0f) return
        for (k in 0..1) {
            val dy = k * separacion
            val medio = Offset((desde.x + hasta.x) / 2f, (desde.y + hasta.y) / 2f + dy + comba * 2f)
            drawPath(Path().apply { moveTo(desde.x, desde.y + dy); quadraticTo(medio.x, medio.y, hasta.x, hasta.y + dy) }, color,
                style = Stroke(grosor, cap = StrokeCap.Round))
        }
    }

    /** Un poste de madera de [alto] px desde [base] (su pie), de [grueso] px, con su travesaño arriba y la sombra a la derecha. */
    fun DrawScope.poste(base: Offset, alto: Float, grueso: Float, color: Color = POSTE) {
        if (alto <= 0f || grueso <= 0f) return
        drawRect(color, Offset(base.x - grueso / 2, base.y - alto), androidx.compose.ui.geometry.Size(grueso, alto))
        drawRect(enSombra(color, 0.5f), Offset(base.x + grueso * 0.15f, base.y - alto), androidx.compose.ui.geometry.Size(grueso * 0.35f, alto))
        drawRect(color, Offset(base.x - grueso * 3.2f, base.y - alto * 0.92f), androidx.compose.ui.geometry.Size(grueso * 6.4f, grueso * 0.6f))
    }

    /**
     * Un muro de ladrillo en [zona]: hiladas de [hilada] px de alto, a soga (cada una corrida medio ladrillo), con la junta
     * de [junta] y los ladrillos de [color] que varían un poco de tono, siempre igual para la misma [semilla].
     */
    fun DrawScope.ladrillos(zona: Rect, hilada: Float, color: Color = COCHE, junta: Color = enSombra(color, 0.55f), semilla: Int = 3) {
        if (zona.width <= 0f || zona.height <= 0f) return
        val alto = max(hilada, 2f)
        val largo = alto * 2.6f
        val j = max(alto * 0.12f, 0.5f)
        clipPath(Path().apply { addRect(zona) }) {
            drawRect(junta, zona.topLeft, zona.size)
            var fila = 0
            var y = zona.top
            while (y < zona.bottom) {
                var col = 0
                var x = zona.left - (if (fila % 2 == 0) 0f else largo / 2)
                while (x < zona.right) {
                    val tono = (azarFijo(col, fila, semilla) - 0.5f) * 0.16f
                    val c = if (tono > 0) alSol(color, tono) else enSombra(color, -tono)
                    drawRect(c, Offset(x + j / 2, y + j / 2), androidx.compose.ui.geometry.Size(largo - j, alto - j))
                    x += largo; col++
                }
                y += alto; fila++
            }
        }
    }

    /**
     * Una persiana de lamas en [zona]: [lamas] tablillas horizontales de [color], cada una con su filo claro arriba y su
     * sombra abajo; [abierta] (de 0 a 1) separa las lamas y deja ver [detras].
     */
    fun DrawScope.persiana(zona: Rect, lamas: Int, color: Color = CASA_GRIS_CLARA, abierta: Float = 0f, detras: Color = PARED_NOCHE) {
        if (zona.width <= 0f || zona.height <= 0f || lamas <= 0) return
        drawRect(detras, zona.topLeft, zona.size)
        val alto = zona.height / lamas
        val lleno = alto * (1f - 0.6f * abierta.coerceIn(0f, 1f))
        for (k in 0 until lamas) {
            val y = zona.top + k * alto
            drawRect(color, Offset(zona.left, y), androidx.compose.ui.geometry.Size(zona.width, lleno))
            drawRect(alSol(color, 0.4f), Offset(zona.left, y), androidx.compose.ui.geometry.Size(zona.width, max(lleno * 0.18f, 0.5f)))
            drawRect(enSombra(color, 0.35f), Offset(zona.left, y + lleno * 0.82f), androidx.compose.ui.geometry.Size(zona.width, max(lleno * 0.18f, 0.5f)))
        }
    }

    /**
     * La placa de un letrero pintado (el texto lo pone quien llama): el fondo [fondo], un filete de [filete] por dentro y
     * la esquina de [radio] px. Sin brillo ni neón: es pintura sobre lámina.
     */
    fun DrawScope.placaLetrero(zona: Rect, fondo: Color = AL_SOL, filete: Color = LETRERO, radio: Float = 3f * density) {
        if (zona.width <= 0f || zona.height <= 0f) return
        val r = CornerRadius(max(radio, 0f))
        drawRoundRect(enSombra(fondo, 0.5f), zona.topLeft + Offset(density, density * 1.5f), zona.size, r)
        drawRoundRect(fondo, zona.topLeft, zona.size, r)
        val m = min(zona.width, zona.height) * 0.08f
        drawRoundRect(filete, zona.topLeft + Offset(m, m), androidx.compose.ui.geometry.Size(zona.width - 2 * m, zona.height - 2 * m),
            CornerRadius(max(radio - m, 0f)), style = Stroke(max(m * 0.45f, 0.75f)))
    }
}

/**
 * La psicodelia (cara INTENSA): las tres tintas fijas (magenta, bermellón y ultramar), el papel (crema o negro de
 * terciopelo), la tinta de grabar, cómo se calientan con la intensidad y los trazos de cartel: las bandas que ondulan, los
 * rayos, el grabado fino de líneas, el registro corrido, el marco de arco y las letras que llenan su forma.
 *
 * Cada tinta tiene un valor de reposo (i = 0), uno medio (0.5) y uno pleno (1), a la misma luminosidad (la tabla de la
 * guía): entre medias se interpola en Oklab (`lerp` de Compose). Las tintas de dibujo nunca llevan texto (el bermellón
 * sobre papel da 3.2:1: vale para dibujo, no para letra).
 */
object TrazosTinta {
    /** Las tres tintas del cartel. */
    enum class Tinta { MAGENTA, BERMELLON, ULTRAMAR }

    /** Los tres papeles de Material que se calientan con la intensidad (los demás no cambian nunca). */
    enum class Rol { PRIMARIO, SECUNDARIO, TERCIARIO }

    /** El papel del cartel y el negro de terciopelo (son el fondo de sus esquemas). */
    val PAPEL = Color(0xFFF2E8D3)
    val NEGRO = Color(0xFF0F0C17)
    /** La tinta azul negra que graba, sobre el papel. */
    val GRABAR = Color(0xFF1E1A3A)
    /** El crema del papel, que hace de cuarta tinta sobre el negro. */
    val CREMA = Color(0xFFF3E9D6)

    /** El papel sobre el que se imprime: crema en el claro, negro de terciopelo en el oscuro. */
    fun papel(oscuro: Boolean): Color = if (oscuro) NEGRO else PAPEL

    /** La tinta que graba (los rayos, la trama, los filetes): la azul negra sobre papel, el crema sobre negro. */
    fun grabado(oscuro: Boolean): Color = if (oscuro) CREMA else GRABAR

    // Cada tinta: reposo, media y plena. Sobre papel y sobre negro (la tabla «Las tintas y su intensidad» de la guía).
    private val MAGENTA_PAPEL = listOf(Color(0xFFA94870), Color(0xFFB73570), Color(0xFFC50E70))
    private val BERMELLON_PAPEL = listOf(Color(0xFFC56851), Color(0xFFD55A3C), Color(0xFFE5481E))
    private val ULTRAMAR_PAPEL = listOf(Color(0xFF3F4C9E), Color(0xFF3A44B3), Color(0xFF3739C9))
    private val MAGENTA_NEGRO = listOf(Color(0xFFDC7BA1), Color(0xFFEC6DA2), Color(0xFFFB5BA2))
    private val BERMELLON_NEGRO = listOf(Color(0xFFE0907B), Color(0xFFEF876B), Color(0xFFFD7C5A))
    private val ULTRAMAR_NEGRO = listOf(Color(0xFF8693D7), Color(0xFF8190EA), Color(0xFF7C8CFC))

    // Los tres papeles de Material: reposo (los del esquema), medio y pleno (la tabla «Qué hace cada cosa con ella»).
    private val PRIMARIO_CLARO = listOf(Color(0xFF974164), Color(0xFFA43164), Color(0xFFB11265))
    private val SECUNDARIO_CLARO = listOf(Color(0xFF404D99), Color(0xFF3B47AD), Color(0xFF383EC1))
    private val TERCIARIO_CLARO = listOf(Color(0xFF6A3E8E), Color(0xFF6F3399), Color(0xFF7428A6))
    private val PRIMARIO_OSCURO = listOf(Color(0xFFDD80A4), Color(0xFFEC73A5), Color(0xFFFA63A5))
    private val SECUNDARIO_OSCURO = listOf(Color(0xFF94A1DB), Color(0xFF909EEB), Color(0xFF8C9CFB))
    private val TERCIARIO_OSCURO = listOf(Color(0xFFB79BE0), Color(0xFFBE95F0), Color(0xFFC58CFF))

    /** De los tres valores (0, 0.5 y 1) al de [i], en dos tramos. */
    private fun tramo(v: List<Color>, i: Float): Color {
        val x = if (i.isNaN()) 0f else i.coerceIn(0f, 1f)
        return if (x <= 0.5f) lerp(v[0], v[1], x * 2f) else lerp(v[1], v[2], (x - 0.5f) * 2f)
    }

    /** Una tinta de dibujo, sobre papel ([oscuro] falso) o sobre negro, con la intensidad [i] (de 0 a 1). */
    fun tinta(t: Tinta, oscuro: Boolean, i: Float): Color = tramo(when (t) {
        Tinta.MAGENTA -> if (oscuro) MAGENTA_NEGRO else MAGENTA_PAPEL
        Tinta.BERMELLON -> if (oscuro) BERMELLON_NEGRO else BERMELLON_PAPEL
        Tinta.ULTRAMAR -> if (oscuro) ULTRAMAR_NEGRO else ULTRAMAR_PAPEL
    }, i)

    /**
     * El primario, el secundario o el terciario de la cara psicodelia con la [intensidad] (de 0 a 1), a luminosidad fija:
     * con i = 0 son los del esquema; el contraste sobre el fondo no baja de 5.3 (claro) ni de 6.4 (oscuro). Los componentes
     * del tema los leen de aquí en lugar del `colorScheme`, que se queda en reposo.
     */
    fun rol(papel: Rol, oscuro: Boolean, intensidad: Float): Color = tramo(when (papel) {
        Rol.PRIMARIO -> if (oscuro) PRIMARIO_OSCURO else PRIMARIO_CLARO
        Rol.SECUNDARIO -> if (oscuro) SECUNDARIO_OSCURO else SECUNDARIO_CLARO
        Rol.TERCIARIO -> if (oscuro) TERCIARIO_OSCURO else TERCIARIO_CLARO
    }, intensidad)

    /**
     * Lo que sale de imprimir una tinta sobre otra (el violeta de magenta sobre ultramar), como en la serigrafía: sobre
     * papel, multiplicando (las tintas se filtran); sobre negro **[Aproximación]**, la mezcla a partes iguales, porque las
     * tintas para luz negra se cubren más de lo que se filtran.
     */
    fun sobreimpresion(a: Color, b: Color, oscuro: Boolean): Color =
        if (oscuro) lerp(a, b, 0.5f) else Color(a.red * b.red, a.green * b.green, a.blue * b.blue, max(a.alpha, b.alpha))

    // ------------------------------------------------------------------------------------------------ la intensidad

    /**
     * Cuánto se mueve y se carga el cartel con la intensidad. Las medidas en dp; las velocidades, por segundo (0 = quieto).
     * Salen de la tabla «Qué hace cada cosa con ella» de la guía, con el máximo **un poco más fuerte** que el de la lámina
     * (decisión 2 del autor): con i = 1, 11 bandas por lado (no 10), amplitud de 32 dp (no 28), trazo de 6.5 dp (no 6),
     * registro corrido 5 dp (no 4), 8 anillos (no 7), el título doblado por 6 dp (no 5) y corrido 3.5 (no 3), y las bandas
     * avanzan una cada 7 s (no 8).
     */
    class Medidas(
        /** Bandas a cada lado de la figura central (7 con i = 0, 9 con 0.5 y 11 con 1). */
        val bandasPorLado: Int,
        /** Amplitud de la onda de las bandas, en dp (6, 17, 32). */
        val amplitud: Float,
        /** Grosor del trazo de las bandas, en dp (3, 5, 6.5). */
        val grosor: Float,
        /** Cuánto se corre el registro de las tintas, en dp (0, 2, 5): el magenta a la derecha, el bermellón a la izquierda. */
        val registro: Float,
        /** Anillos alternos alrededor del disco (0, 5, 8). */
        val anillos: Int,
        /** Opacidad de la tercera tinta, el bermellón: 0 hasta i = 0.25, sube hasta 1 en i = 0.5 (con i = 0 se imprime a dos tintas). */
        val bermellon: Float,
        /** Con i ≥ 0.9, las bandas se salen del marco del cartel; antes, todo queda dentro. */
        val fueraDelMarco: Boolean,
        /** La onda que dobla los títulos grandes decorativos, en dp (0, 2, 6), y lo que se corre su segunda pasada en magenta (0, 1.5, 3.5). */
        val ondaTitulo: Float,
        val registroTitulo: Float,
        /** El fondo vivo respira con esta amplitud, en dp (2, siempre), en un ciclo de 12 s. */
        val respiro: Float,
        /** Cuántas bandas avanzan por segundo (0 con i = 0: solo respira; 1/20 con 0.5; 1/7 con 1). */
        val bandasPorSegundo: Float,
        /** Cuántas vueltas da el disco por segundo (1/180 con i = 0; 1/120 con 0.5; 1/50 con 1). */
        val vueltasPorSegundo: Float,
    )

    private fun tramo(a: Float, b: Float, c: Float, i: Float): Float {
        val x = if (i.isNaN()) 0f else i.coerceIn(0f, 1f)
        return if (x <= 0.5f) a + (b - a) * x * 2f else b + (c - b) * (x - 0.5f) * 2f
    }

    /** Las [Medidas] con la intensidad [i] (de 0 a 1); entre los valores de la tabla se interpola y los enteros se redondean. */
    fun medidas(i: Float): Medidas {
        val x = if (i.isNaN()) 0f else i.coerceIn(0f, 1f)
        return Medidas(
            bandasPorLado = tramo(7f, 9f, 11f, x).roundToInt(),
            amplitud = tramo(6f, 17f, 32f, x),
            grosor = tramo(3f, 5f, 6.5f, x),
            registro = tramo(0f, 2f, 5f, x),
            anillos = tramo(0f, 5f, 8f, x).roundToInt(),
            bermellon = ((x - 0.25f) / 0.25f).coerceIn(0f, 1f),
            fueraDelMarco = x >= 0.9f,
            ondaTitulo = tramo(0f, 2f, 6f, x),
            registroTitulo = tramo(0f, 1.5f, 3.5f, x),
            respiro = 2f,
            bandasPorSegundo = tramo(0f, 1f / 20f, 1f / 7f, x),
            vueltasPorSegundo = tramo(1f / 180f, 1f / 120f, 1f / 50f, x),
        )
    }

    /**
     * Hacia dónde se corre una tinta con el registro [registro] (en dp; ver [Medidas.registro]): el magenta a la derecha y
     * un poco arriba, el bermellón a la izquierda y un poco abajo, el ultramar queda en su sitio (como en la lámina).
     */
    fun corrimiento(t: Tinta, registro: Float): Offset = when (t) {
        Tinta.MAGENTA -> Offset(registro, -registro / 2f)
        Tinta.BERMELLON -> Offset(-registro, registro / 2f)
        Tinta.ULTRAMAR -> Offset.Zero
    }

    /** Dibuja [bloque] corrido como la tinta [t] con el registro [registro] (en dp). */
    inline fun DrawScope.conRegistro(t: Tinta, registro: Float, bloque: DrawScope.() -> Unit) {
        val d = corrimiento(t, registro)
        if (d == Offset.Zero) bloque() else translate(d.x * density, d.y * density) { bloque() }
    }

    // ------------------------------------------------------------------------------------------------ trazos

    /**
     * Las bandas que fluyen: [n] líneas a cada lado de [centro], en espejo, de arriba ([centro].y − [alto] / 2) abajo, que
     * se juntan en los extremos y se abren alrededor de la figura central ([abre], de 0 a 1). [separacion] entre bandas,
     * [amplitud] de la onda y [grosor] del trazo, en px; [frecuencia], ondas en todo el alto; [fase], en vueltas (de 0 a
     * 1, cierra sin salto). Sin relleno: tinta plana, sin sombrear.
     */
    fun DrawScope.bandas(centro: Offset, alto: Float, n: Int, separacion: Float, amplitud: Float, grosor: Float, color: Color,
                         frecuencia: Float = 1.4f, fase: Float = 0f, abre: Float = 0.9f) {
        if (alto <= 0f || n <= 0 || grosor <= 0f || !alto.isFinite()) return
        val paso = max(alto / 120f, 0.5f)
        val arriba = centro.y - alto / 2f
        val dos = 2f * PI.toFloat()
        val st = Stroke(grosor, cap = StrokeCap.Round, join = StrokeJoin.Round)
        for (lado in intArrayOf(-1, 1)) for (k in 0 until n) {
            val p = Path()
            var y = 0f
            var primero = true
            while (y <= alto + 0.01f) {
                val t = y / alto
                val ab = sin(PI.toFloat() * t).coerceAtLeast(0f).pow(1.6f)
                val x = centro.x + lado * (separacion * (k + 0.6f) * (0.35f + abre * ab)) +
                    lado * amplitud * sin(frecuencia * t * dos + fase * dos + k * 0.12f) * ab
                if (primero) { p.moveTo(x, arriba + y); primero = false } else p.lineTo(x, arriba + y)
                y += paso
            }
            drawPath(p, color, style = st)
        }
    }

    /**
     * Los rayos que ondulan desde [centro]: [n] sectores, de los que se llenan los pares, entre los radios [r0] y [r1]; sus
     * bordes son ondas de [amplitud] px con [ondas] vueltas a lo largo del rayo, corridas [fase] (en radianes). [lleno] es
     * la parte del sector que ocupa cada rayo. Devuelve el contorno, para llenarlo de tinta plena o de [trama].
     */
    fun rayos(centro: Offset, r0: Float, r1: Float, n: Int, amplitud: Float, ondas: Float, fase: Float = 0f, lleno: Float = 1f): Path {
        val p = Path()
        if (n < 2 || r1 <= r0 || r0 < 0f || !r1.isFinite()) return p
        val dos = 2f * PI.toFloat()
        val pasos = 40
        for (k in 0 until n step 2) {
            val a0 = dos * k / n
            val a1 = a0 + dos / n * lleno.coerceIn(0f, 1f)
            for (s in 0..pasos) {
                val r = r0 + (r1 - r0) * s / pasos
                val da = amplitud * sin(ondas * s / pasos * dos + fase) / max(r, 1f)
                val x = centro.x + r * kotlin.math.cos(a0 + da); val y = centro.y + r * sin(a0 + da)
                if (s == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            for (s in pasos downTo 0) {
                val r = r0 + (r1 - r0) * s / pasos
                val da = amplitud * sin(ondas * s / pasos * dos + fase) / max(r, 1f)
                p.lineTo(centro.x + r * kotlin.math.cos(a1 + da), centro.y + r * sin(a1 + da))
            }
            p.close()
        }
        return p
    }

    /**
     * El grabado fino de líneas: llena [forma] con rayas paralelas de [grosor] px cada [paso] px, giradas [angulo] grados
     * (0 = horizontales). Es la trama de los medios tonos del cartel a una sola tinta.
     */
    fun DrawScope.trama(forma: Path, paso: Float, grosor: Float, color: Color, angulo: Float = 0f) {
        val b = forma.getBounds()
        if (b.isEmpty || grosor <= 0f || !b.width.isFinite() || !b.height.isFinite()) return
        val p = max(paso, 1f)
        val r = sqrt(b.width * b.width + b.height * b.height) / 2f + p
        val c = b.center
        clipPath(forma) {
            rotate(angulo, c) {
                var y = c.y - r
                while (y <= c.y + r) {
                    drawLine(color, Offset(c.x - r, y), Offset(c.x + r, y), grosor)
                    y += p
                }
            }
        }
    }

    /** El marco de arco del cartel: arriba, esquinas de [radio] px; abajo, casi rectas ([poco] px). */
    fun arco(caja: Rect, radio: Float, poco: Float = 2f): Path = Path().apply {
        if (caja.isEmpty) return@apply
        val r = CornerRadius(radio.coerceIn(0f, min(caja.width, caja.height) / 2f))
        val q = CornerRadius(poco.coerceIn(0f, min(caja.width, caja.height) / 2f))
        addRoundRect(RoundRect(caja, topLeft = r, topRight = r, bottomRight = q, bottomLeft = q))
    }

    // ------------------------------------------------------------------------------------------------ letras de bloque

    /** La silueta que llenan las [Letras]: recta, en barril (más altas en el medio), en arco (arriba curva) o en cuenco. */
    enum class FormaLetras { RECTA, BARRIL, ARCO, CUENCO }

    /** De la forma a lo que ocupa la letra en la caja, a lo ancho [u] (de 0 a 1): su arriba y su abajo, en fracción del alto. */
    private fun limites(f: FormaLetras, u: Float): Pair<Float, Float> {
        val s = sin(PI.toFloat() * u.coerceIn(0f, 1f))
        return when (f) {
            FormaLetras.RECTA -> 0f to 1f
            FormaLetras.BARRIL -> 0.2f * (1f - s) to 1f - 0.2f * (1f - s)
            FormaLetras.ARCO -> 0.32f * (1f - s) to 1f
            FormaLetras.CUENCO -> 0f to 1f - 0.32f * (1f - s)
        }
    }

    /** Las letras de un título, medidas una por una, listas para [llenarForma]. Se miden una vez (en caché), no en cada cuadro. */
    class Letras internal constructor(internal val glifos: List<TextLayoutResult>, internal val espacio: Float)

    /** Mide [texto] letra por letra con [estilo] (la de títulos de la cara psicodelia, `LetraExtra.bloque`). [espacio] entre letras, en px. */
    fun letras(medidor: TextMeasurer, texto: String, estilo: TextStyle, espacio: Float = 0f): Letras =
        Letras(texto.map { medidor.measure(it.toString(), estilo) }, espacio)

    /**
     * Las letras que llenan su forma, como en los carteles de 1966 y 1967: el título se estira a lo ancho de [caja] y cada
     * letra, a lo alto de la [forma] en su sitio. [onda] (en px) la dobla con [frecuencia] ondas en todo el ancho, corrida
     * [fase] (en vueltas). Solo para títulos decorativos grandes: el texto y las cifras de datos no se deforman nunca.
     */
    fun DrawScope.llenarForma(letras: Letras, caja: Rect, color: Color, forma: FormaLetras = FormaLetras.RECTA, onda: Float = 0f,
                              frecuencia: Float = 1f, fase: Float = 0f) {
        val g = letras.glifos
        if (g.isEmpty() || caja.isEmpty || !caja.width.isFinite() || !caja.height.isFinite()) return
        val total = g.sumOf { it.size.width.toDouble() }.toFloat() + letras.espacio * (g.size - 1)
        if (total <= 0f) return
        val sx = caja.width / total
        val dos = 2f * PI.toFloat()
        var x = 0f
        for (l in g) {
            val w = l.size.width.toFloat(); val h = l.size.height.toFloat()
            if (w > 0f && h > 0f) {
                val u = (x + w / 2f) / total
                val (a, b) = limites(forma, u)
                val alto = (b - a) * caja.height
                val dy = onda * sin(dos * (u * frecuencia + fase))
                val sy = alto / h
                withTransform({
                    translate(caja.left + x * sx, caja.top + a * caja.height + dy)
                    scale(sx, sy, Offset.Zero)
                }) { drawText(l, color) }
            }
            x += w + letras.espacio
        }
    }
}

/** Un número de 0 a 1 que sale siempre igual de ([a], [b], [semilla]): el azar fijo del grano y de los ladrillos. */
private fun azarFijo(a: Int, b: Int, semilla: Int): Float {
    var h = a * 374761393 + b * 668265263 + semilla * 1442695041
    h = (h xor (h ushr 13)) * 1274126177
    h = h xor (h ushr 16)
    return (h and 0xFFFFFF) / 16777215f
}
