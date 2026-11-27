package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * La paleta del cuarto de Terminal, la misma del Ideario (`IdeasCiber`): la noche azulada, la pintura azul pizarra de la pared, el
 * aglomerado perforado, el beis de la chapa y del plástico de las máquinas, el negro de rieles y zócalo, el acero, el fósforo
 * cian y los pilotos; el papel continuo con sus rayas verdes, las notas amarillas, el cable gris y el café. Lo de la biblioteca:
 * el aluminio de los portaetiquetas, el fósforo ámbar del monitor del suelo, el linóleo a cuadros y la luna del salvapantallas.
 * Dos temperaturas: lo frío es la pared, el cian y la luna; lo cálido, el beis, el aglomerado, el rojo de la regleta y el ámbar.
 */
internal object PaletaCiber {
    /** La noche del cuarto: negro azulado (la del Ideario). */
    val NOCHE = Color(0xFF060A12)
    /** La pintura azul pizarra de la pared y el aglomerado pardo del tablero perforado. */
    val PARED = Color(0xFF34405A)
    val TABLERO = Color(0xFF8A6E4C)
    /** El plástico beis amarillento de las máquinas y su sombra; la chapa beis de la estantería, algo más gris. */
    val BEIS = Color(0xFFDCCDA2)
    val BEIS_OSC = Color(0xFFA8956A)
    val CHAPA = Color(0xFFD2C6A0)
    /** El negro de los rieles, del zócalo y de los cables; el acero que asoma y su sombra; el aluminio de los canales. */
    val NEGRO = Color(0xFF262A33)
    val ACERO = Color(0xFF8C939E)
    val ACERO_OSC = Color(0xFF4E5560)
    val ALUMINIO = Color(0xFFB7BEC7)
    /** El fósforo cian (el del tema) y su brillo; la luz que echa en el cuarto. */
    val CIAN = Color(0xFF00E5FF)
    val CIAN_CLARO = Color(0xFFBFF8FF)
    val CIAN_LUZ = Color(0xFF45E4F2)
    /** Los pilotos. */
    val VERDE = Color(0xFF5CFF8A)
    val AMBAR = Color(0xFFFFB84F)
    val ROJO = Color(0xFFFF4A3D)
    /** El fósforo ámbar del monitor del suelo y la luz que echa. */
    val FOSFORO_AMBAR = Color(0xFFFFA22E)
    val AMBAR_LUZ = Color(0xFFFFA445)
    /** La luna del salvapantallas (los Sueños) y el azul hondo de su fondo. */
    val LUNA = Color(0xFFB4C8FF)
    val AZUL_SALVA = Color(0xFF0B1E58)
    /** Las notas adhesivas, el papel continuo y sus rayas verdes. */
    val NOTA = Color(0xFFEBD65A)
    val PAPEL = Color(0xFFE8E4D2)
    val RAYA = Color(0xFFA6D2A0)
    /** El cable gris, el café, el cartón y la tinta de bolígrafo. */
    val CABLE = Color(0xFF9EA09B)
    val CAFE = Color(0xFF5A3822)
    val CARTON = Color(0xFFA48B63)
    val TINTA = Color(0xFF15182C)
    /** El linóleo del suelo, a cuadros: el gris azulado del Ideario y uno más cálido. */
    val LINO = Color(0xFF59606E)
    val LINO_B = Color(0xFF7C7566)
    /** La placa verde de los circuitos. */
    val PLACA = Color(0xFF2F7A4E)
}

// ---------------------------------------------------------------- la luz del cuarto

/**
 * La luz del cuarto en un estado. En el diario ([noche] falso): la pantalla de la transportable (cian, arriba a la izquierda), el
 * interruptor rojo de la regleta del costado (a media altura) y el monitor ámbar del suelo. En los Sueños: el salvapantallas de la
 * luna (azul pálido, manda), la regleta baja y el monitor del suelo casi apagado. El tema es solo oscuro: el modo claro no cambia
 * nada.
 */
internal class AireCiber(val noche: Boolean) {
    private val p = PaletaCiber
    /** El color de cada luz: la pantalla, la regleta y el monitor ámbar. */
    val colores: Array<Color> = if (noche) arrayOf(p.LUNA, p.ROJO, p.AMBAR_LUZ) else arrayOf(p.CIAN_LUZ, p.ROJO, p.AMBAR_LUZ)
    /** La fuerza de cada una. */
    val fuerzas: FloatArray = if (noche) floatArrayOf(0.88f, 0.78f, 0.3f) else floatArrayOf(1.4f, 1.2f, 1.05f)
    /** Cuánto tiñe cada luz lo que toca (el cian, poco: el beis sigue siendo beis; el rojo y el ámbar, más). */
    private val tintes: FloatArray = if (noche) floatArrayOf(0.32f, 0.55f, 0.42f) else floatArrayOf(0.2f, 0.55f, 0.4f)

    /** Lo hondo de un color: oscuro pero de su mismo tono, apenas hacia la noche azul del cuarto (el beis, a un pardo azulado). */
    fun hondo(c: Color): Color = if (noche) Color(c.red * 0.15f + 0.008f, c.green * 0.15f + 0.014f, c.blue * 0.17f + 0.04f, c.alpha)
        else Color(c.red * 0.19f + 0.008f, c.green * 0.17f + 0.013f, c.blue * 0.18f + 0.034f, c.alpha)

    /** Cuánto de su color conserva algo con [k] de luz: sube deprisa cerca de la luz y cae a casi nada lejos. */
    private fun cuanto(k: Float): Float = (1.3f * k.coerceIn(0f, 1.3f).pow(1.35f)).coerceAtMost(0.97f)

    /** Un color con la luz [k] de color [l] y [tinte]: teñido por ella, encendido cerca y hundido lejos. */
    fun luz(c: Color, k: Float, l: Color, tinte: Float): Color {
        val kk = k.coerceIn(0f, 1.3f)
        val mr = 1f + (l.red - 1f) * tinte; val mg = 1f + (l.green - 1f) * tinte; val mb = 1f + (l.blue - 1f) * tinte
        val brilla = 0.07f * min(kk, 1f) * min(kk, 1f)
        val v = Color(min(1f, c.red * mr + l.red * brilla), min(1f, c.green * mg + l.green * brilla), min(1f, c.blue * mb + l.blue * brilla), c.alpha)
        return mezcla(hondo(c), v, cuanto(kk))
    }

    fun tinte(i: Int): Float = tintes[i]

    /** La sombra: la noche del cuarto, nunca negro puro. */
    fun sombra(a: Float): Color = p.NOCHE.copy(alpha = a.coerceIn(0f, 1f))
    /** El filo de luz en lo que mira a la luz: del color de la luz, aclarado; algo siempre, más cerca de ella. */
    fun filo(a: Float, k: Float, l: Color): Color = mezcla(l, Color.White, 0.45f).copy(alpha = (a * (0.16f + 0.9f * k.coerceIn(0f, 1f))).coerceIn(0f, 1f))
}

/** La luz que le toca a un punto: cuánta ([k]), de qué color ([l]), cuánto tiñe ([tn]) y de qué lado le llega ([hacia]). */
internal class TonoCiber(val a: AireCiber, val k: Float, val l: Color, val tn: Float, val hacia: Float) {
    fun c(col: Color, mas: Float = 1f): Color = a.luz(col, k * mas, l, tn)
    fun s(al: Float): Color = a.sombra(al)
    fun b(al: Float, mas: Float = 1f): Color = a.filo(al, k * mas, l)
    val izq: Boolean get() = hacia < 0f
}

/**
 * Lo que llega de cada luz a un punto ([fx], [fy] en anchos del mueble), en [k]: la pantalla (se abre hacia abajo, hacia el
 * cuarto), la regleta (un charco corto) y el monitor ámbar del suelo (sube más que se abre). [regY] y [ambY]: la altura de la
 * regleta y la del monitor ámbar, en anchos.
 */
internal fun lucesCiber(fx: Float, fy: Float, a: AireCiber, regY: Float, ambY: Float, k: FloatArray) {
    val m = MuebleEstanteCiber
    // Cada charco cae como una campana: fuerte cerca, deprisa a media distancia y casi nada lejos (entre dos charcos, lo hondo).
    val dx0 = fx - m.PANT_X; val dy0 = (fy - m.PANT_Y) * (if (fy > m.PANT_Y) 0.82f else 1.2f)
    val s0 = if (a.noche) 0.6f else 0.52f
    k[0] = a.fuerzas[0] * exp(-(dx0 * dx0 + dy0 * dy0) / (s0 * s0))
    val dx1 = (fx - m.REG_X) * 1.15f; val dy1 = fy - regY
    k[1] = a.fuerzas[1] * exp(-(dx1 * dx1 + dy1 * dy1) / (0.21f * 0.21f))
    val dx2 = fx - m.AMB_X; val dy2 = (fy - ambY) * (if (fy < ambY) 0.7f else 1.1f)
    k[2] = a.fuerzas[2] * exp(-(dx2 * dx2 + dy2 * dy2) / (0.43f * 0.43f))
}

/** El tono en ([fx], [fy]) en anchos del mueble. */
internal fun tonoCiber(fx: Float, fy: Float, a: AireCiber, regY: Float, ambY: Float): TonoCiber {
    val k = FloatArray(3)
    lucesCiber(fx, fy, a, regY, ambY, k)
    var suma = 0f; var r = 0f; var g = 0f; var b = 0f; var tn = 0f; var lado = 0f
    val m = MuebleEstanteCiber
    val xs = floatArrayOf(m.PANT_X, m.REG_X, m.AMB_X)
    for (i in 0 until 3) {
        val ki = k[i]; if (ki <= 0f) continue
        val col = a.colores[i]
        suma += ki; r += col.red * ki; g += col.green * ki; b += col.blue * ki; tn += a.tinte(i) * ki
        lado += ki * (if (xs[i] > fx) 1f else -1f)
    }
    if (suma < 1e-4f) return TonoCiber(a, 0f, a.colores[0], 0.3f, -1f)
    return TonoCiber(a, suma.coerceAtMost(1.3f), Color(r / suma, g / suma, b / suma), tn / suma, if (lado >= 0f) 1f else -1f)
}

/**
 * Las medidas del cuarto que comparten las piezas, en píxeles: el ancho [w] y el alto [h] del lienzo, el pie de la corona [ch],
 * el borde de dentro de los costados ([x0], [x1]), el pie de la última tabla ([fondo]), una repisa con su tabla ([fila]) y
 * cuántas repisas hay ([n]). Y el tono en cada punto.
 */
internal class MarcoCiber(val w: Float, val h: Float, val ch: Float, val x0: Float, val x1: Float, val fondo: Float, val fila: Float,
                          val n: Int, val a: AireCiber) {
    val m = MuebleEstanteCiber
    /** Donde se apoya lo de encima: el canto de delante de la tapa del remate y su canto de atrás. */
    val apoyo: Float get() = ch - w * m.CORNISA
    val apoyoAtras: Float get() = apoyo - w * 0.024f
    /** Los bordes del mueble (la pared asoma fuera). */
    val ml: Float get() = w * m.BORDE
    val mr: Float get() = w - w * m.BORDE
    /** El cuerpo bajo: el armario de dos puertas bajo la última tabla, el zócalo, el rincón con el suelo y donde pisan las patas. */
    val armario: Float get() = fondo + w * 0.15f
    val zocalo: Float get() = fondo + w * 0.178f
    val suelo: Float get() = fondo + w * 0.168f
    val pisan: Float get() = fondo + w * 0.186f
    /** La regleta y el monitor ámbar, en anchos. */
    val regY: Float get() = (ch + fila * (filaReglaCiber(n) + 0.18f)) / w
    val ambY: Float get() = fondo / w + m.AMB_DY

    fun t(p: Offset): TonoCiber = tonoCiber(p.x / w, p.y / w, a, regY, ambY)
    fun t(x: Float, y: Float): TonoCiber = tonoCiber(x / w, y / w, a, regY, ambY)
    fun c(col: Color, p: Offset, mas: Float = 1f): Color = t(p).c(col, mas)
}

/** En qué repisa va la regleta del costado (arriba de ella), de [n]. */
internal fun filaReglaCiber(n: Int): Int = floor(n * 0.45f).toInt().coerceIn(0, maxOf(0, n - 1))

/** Para un volumen: dónde cae en anchos del mueble ([repisa] desde arriba y su centro [centro] de 0 a 1). */
internal fun lugarVolumenCiber(repisa: Int, centro: Float, alto: Float = 0.55f): Offset {
    val m = MuebleEstanteCiber
    return Offset(m.LADO + centro * (1f - 2 * m.LADO), m.CORONA + (repisa + alto) * m.FILA / m.ANCHO_NOMINAL)
}

/** La altura de la regleta y del monitor ámbar, para un volumen que no sabe cuánto mide el estante (lo supone de un teléfono). */
internal fun regNominalCiber(repisas: Int): Float {
    val m = MuebleEstanteCiber
    return m.CORONA + (filaReglaCiber(repisas) + 0.18f) * m.FILA / m.ANCHO_NOMINAL
}
internal fun ambNominalCiber(repisas: Int): Float {
    val m = MuebleEstanteCiber
    return m.CORONA + repisas * m.FILA / m.ANCHO_NOMINAL + m.AMB_DY
}

// ---------------------------------------------------------------- bandas con su luz

/** Una banda horizontal de [col] de [x0] a [x1] entre [y0] e [y1], con la luz que le llega a lo largo (seis paradas, un trazo). */
internal fun DrawScope.bandaCiber(x0: Float, x1: Float, y0: Float, y1: Float, col: Color, g: MarcoCiber, mas: Float = 1f, ym: Float = (y0 + y1) / 2) {
    if (x1 <= x0 || y1 <= y0) return
    val paradas = Array(6) { i -> val f = i / 5f; f to g.t(x0 + (x1 - x0) * f, ym).c(col, mas) }
    drawRect(Brush.horizontalGradient(*paradas, startX = x0, endX = x1), Offset(x0, y0), Size(x1 - x0, y1 - y0))
}

/** Una columna vertical de [col] de [y0] a [y1], con la luz que le llega a lo alto ([n] paradas). */
internal fun DrawScope.columnaCiber(x0: Float, x1: Float, y0: Float, y1: Float, col: Color, g: MarcoCiber, mas: Float = 1f, n: Int = 8) {
    if (x1 <= x0 || y1 <= y0) return
    val xm = (x0 + x1) / 2
    val paradas = Array(n) { i -> val f = i / (n - 1f); f to g.t(xm, y0 + (y1 - y0) * f).c(col, mas) }
    drawRect(Brush.verticalGradient(*paradas, startY = y0, endY = y1), Offset(x0, y0), Size(x1 - x0, y1 - y0))
}

// ---------------------------------------------------------------- lo fijo pintado una vez

/**
 * La corona pintada una vez por tamaño y estado, en dos ranuras (el diario y los Sueños). Pegar una imagen cuesta mucho menos que
 * repetir la pared, el banderín y el bodegón en cada dibujo. Unos 2.7 MB por ranura en un teléfono de 1080 px.
 */
internal object CapaCiber {
    private val claves = arrayOfNulls<String>(2)
    private val imagenes = arrayOfNulls<ImageBitmap>(2)

    /**
     * Pega la imagen de [r] (la pinta antes si su clave cambió). [debajo], si va, se pinta primero, sin el recorte de [r], y la
     * imagen sigue hasta [abajo]: es lo que cubre la fila de la junta que [r] corta a medio píxel.
     */
    fun DrawScope.pegar(ranura: Int, clave: String, r: Rect, debajo: (DrawScope.() -> Unit)? = null, abajo: Float = r.bottom,
                        pintar: DrawScope.() -> Unit) {
        val ancho = r.width.roundToInt(); val alto = r.height.roundToInt()
        if (ancho < 1 || alto < 1 || ranura !in 0..1) return
        var img = imagenes[ranura]
        if (img == null || claves[ranura] != clave) {
            val base = ImageBitmap(ancho, alto)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(base), Size(ancho.toFloat(), alto.toFloat())) {
                translate(-r.left, -r.top) { clipRect(r.left, r.top, r.right, r.bottom) { pintar() } }
            }
            val nueva = if (debajo == null) base else ImageBitmap(ancho, maxOf(alto, ceil(abajo - r.top).toInt())).also { larga ->
                CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(larga), Size(ancho.toFloat(), alto.toFloat())) {
                    translate(-r.left, -r.top) { debajo() }
                    drawImage(base)
                }
            }
            imagenes[ranura] = nueva; claves[ranura] = clave; img = nueva
        }
        drawImage(img, Offset(r.left, r.top))
    }
}

/** La fila de la junta en [y], de [x0] a [x1], de [c], debajo de las dos piezas que se tocan ahí; si [y] es entero, nada. */
internal fun DrawScope.filaJuntaCiber(y: Float, x0: Float, x1: Float, c: Color) {
    val f = floor(y)
    if (f == y || x1 <= x0) return
    drawRect(c, Offset(x0, f), Size(x1 - x0, 1f))
}

/** La columna de la junta en [x], de [y0] a [y1], de [c]; si [x] es entero, nada. */
internal fun DrawScope.columnaJuntaCiber(x: Float, y0: Float, y1: Float, c: Color) {
    val f = floor(x)
    if (f == x || y1 <= y0) return
    drawRect(c, Offset(f, y0), Size(1f, y1 - y0))
}

// ---------------------------------------------------------------- ayudantes de forma

internal fun poligonoCiber(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** La sombra de lo que pisa: un óvalo corrido hacia donde no da la luz. */
internal fun DrawScope.contactoCiber(centro: Offset, ancho: Float, alto: Float, t: TonoCiber, fuerza: Float = 0.55f) =
    drawOval(t.s(fuerza), Offset(centro.x - ancho * (if (t.izq) 0.4f else 0.6f), centro.y - alto / 2), Size(ancho, alto))

/** Un tornillo o un remache de cabeza redonda: su sombra, la cabeza y el punto de luz. */
internal fun DrawScope.remacheCiber(p: Offset, r: Float, t: TonoCiber, metal: Color = PaletaCiber.ACERO) {
    drawCircle(t.s(0.5f), r * 1.05f, p + Offset(r * (if (t.izq) 0.45f else -0.45f), r * 0.5f))
    drawCircle(t.c(metal), r, p)
    drawCircle(t.b(0.85f), r * 0.4f, p + Offset(r * (if (t.izq) -0.3f else 0.3f), -r * 0.3f))
}

/** Un piloto: encendido, con su halo chico y su brillo; apagado, del color hundido con un reflejo. */
internal fun DrawScope.pilotoCiber(c: Offset, r: Float, col: Color, encendido: Boolean, t: TonoCiber) {
    if (encendido) {
        drawCircle(col.copy(alpha = 0.16f), r * 3.4f, c)
        drawCircle(col.copy(alpha = 0.34f), r * 1.9f, c)
        drawCircle(col, r, c)
        drawCircle(Color.White.copy(alpha = 0.75f), r * 0.38f, c - Offset(r * 0.3f, r * 0.3f))
    } else {
        drawCircle(t.c(mezcla(col, PaletaCiber.NOCHE, 0.78f)), r, c)
        drawCircle(t.b(0.45f), r * 0.32f, c - Offset(r * 0.32f, r * 0.32f))
    }
}

/**
 * El lado de luz y el de sombra de un cuerpo dentro de [r] (ir dentro de su recorte): la franja de luz del lado que mira a la luz
 * y la de sombra del otro, con su filo.
 */
internal fun DrawScope.planosCiber(r: Rect, t: TonoCiber, fuerza: Float = 1f) {
    val luzIzq = t.izq
    val xl = if (luzIzq) r.left else r.right - r.width * 0.24f
    drawRect(t.b(0.18f * fuerza), Offset(xl, r.top), Size(r.width * 0.24f, r.height))
    drawRect(t.b(0.36f * fuerza), Offset(if (luzIzq) r.left + r.width * 0.06f else r.right - r.width * 0.11f, r.top), Size(r.width * 0.05f, r.height))
    val xs = if (luzIzq) r.right - r.width * 0.34f else r.left
    drawRect(t.s(0.28f * fuerza), Offset(xs, r.top), Size(r.width * 0.34f, r.height))
    drawRect(t.s(0.22f * fuerza), Offset(if (luzIzq) r.right - r.width * 0.12f else r.left, r.top), Size(r.width * 0.12f, r.height))
}

/** Garabatos de bolígrafo: [n] renglones ondulados dentro de [r] (formas, no letras). */
internal fun DrawScope.garabatosCiber(r: Rect, n: Int, col: Color, semilla: Int, grosor: Float) {
    if (n <= 0 || r.width <= 1f || r.height <= 1f) return
    for (k in 0 until n) {
        val y = r.top + r.height * (k + 0.5f) / n
        val fin = r.left + r.width * (0.5f + 0.5f * azar(semilla, k))
        val p = Path()
        var x = r.left
        p.moveTo(x, y)
        var j = 0
        val paso = maxOf(1f, r.height / n * 0.45f)
        while (x < fin) {
            val nx = minOf(x + paso, fin)
            val dy = r.height / n * 0.22f * (if (j % 2 == 0) -1f else 1f) * (0.5f + azar(semilla + k, j))
            p.cubicTo(x + paso * 0.3f, y + dy, nx - paso * 0.3f, y + dy, nx, y)
            x = nx; j++
        }
        drawPath(p, col, style = androidx.compose.ui.graphics.drawscope.Stroke(grosor, cap = androidx.compose.ui.graphics.StrokeCap.Round))
    }
}

/**
 * El dibujo del estante del diario de Terminal (lo usa `EstanteCiber`, en `BibliotecaContemporanea.kt`): el mismo cuarto del
 * Ideario, de madrugada, en la pared de al lado del bastidor. Es una estantería de oficina de chapa pintada de beis, recogida del
 * banco cuando la tiraron: el remate de canto redondo con su friso de rejilla troquelada y la placa de aluminio; los costados de
 * chapa doblada con su columna de ranuras; las repisas con el canal portaetiquetas de aluminio (el mes, en cinta de rotular); el
 * fondo de tablero perforado de aglomerado; abajo, el armario de dos puertas con cerradura, el zócalo negro y las patas. Contra la
 * pared azul pizarra con el banderín impreso y los cables. Encima, la computadora transportable (su pantalla es la luz del
 * cuadro), la impresora de agujas con el papel continuo, las cajas de disquetes con la taza y el vaso de lápices. En el costado
 * derecho, la regleta con su interruptor rojo. Al pie, el linóleo, el monitor ámbar encendido en el suelo, la caja del papel y la
 * papelera. La pared y lo colgado están en `MuebleEstanteCiberMuro.kt`; el remate y el bodegón, en `MuebleEstanteCiberCorona.kt`;
 * los casilleros, costados, tablas y la regleta, en `MuebleEstanteCiberMueble.kt`; lo de los huecos, en
 * `MuebleEstanteCiberHuecos.kt`; el pie, en `MuebleEstanteCiberPie.kt`; las entradas, en `MuebleEstanteCiberVolumenes.kt`.
 */
internal object MuebleEstanteCiber {
    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteCiber` las lee al dibujar)

    val CORONA = 0.62f
    val BASE = 0.36f
    val LADO = 0.072f
    /** La franja de pared que asoma a cada lado de la estantería, en anchos. */
    val BORDE = 0.022f
    val ALTO_REPISA: Dp = 96.dp
    val TABLA: Dp = 20.dp
    val SEPARA: Dp = 3.dp
    /** El remate, en anchos: de donde pisan las cosas al arranque de las repisas. */
    val CORNISA = 0.105f

    fun ancho(palabras: Int): Dp = (40 + (palabras / 45).coerceAtMost(18)).dp

    /** Cuál de los tres es una entrada: 0, el disquete de 5¼ en su funda; 1, la caja de disquetes; 2, la bolsa de cierre. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla, 91)
        return if (x < 0.4f) 0 else if (x < 0.7f) 1 else 2
    }

    /** La funda con su disquete, la más alta; la caja, con su tapa; la bolsa, blanda y algo sentada. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.85f + 0.07f * azar(semilla, 1)
        1 -> 0.8f + 0.07f * azar(semilla, 1)
        else -> 0.8f + 0.06f * azar(semilla, 1) + 0.03f * (palabras / 800f).coerceIn(0f, 1f)
    }

    // ---------------------------------------------------------------- la luz (en anchos del mueble, desde arriba a la izquierda)

    /** La pantalla de la transportable, en la corona. */
    val PANT_X = 0.25f
    val PANT_Y = 0.385f
    /** La regleta del costado derecho: su x (la de su interruptor). */
    val REG_X = 0.955f
    /** El monitor ámbar del suelo: su x y la altura de su pantalla bajo la última tabla, en anchos. */
    val AMB_X = 0.14f
    val AMB_DY = 0.24f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble: el de un teléfono. */
    val ANCHO_NOMINAL = 400f
    /** Una repisa con su tabla, en dp. */
    val FILA = 116f

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireCiber(noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * u
        val fondo = if (n > 0) d.tablas.last().bottom else ch
        val g = MarcoCiber(w, h, ch, d.izquierda.right, d.derecha.left, fondo, fila, n, a)

        // ---- lo fijo de arriba (la pared, el banderín, lo colgado, el remate y el bodegón), pintado una vez por tamaño y estado.
        // Si el borde de la corona cae a medio píxel, la imagen llega al píxel entero de abajo y, debajo de esa fila, va la
        // corona corrida un píxel: así no asoma el fondo entre la corona y el cuerpo.
        val clave = "${w.toInt()}|${ch.toInt()}|${fila.toInt()}|$n|${fondo.toInt()}|$noche|$u"
        val chI = ceil(ch)
        with(CapaCiber) {
            val debajo: (DrawScope.() -> Unit)? = if (chI > ch && chI >= 2f) {
                { clipRect(0f, chI - 1f, w, chI) { translate(0f, 1f) { coronaCiber(g); granoMueble(Rect(0f, 0f, w, ch), true) } } }
            } else null
            pegar(if (noche) 1 else 0, clave, Rect(0f, 0f, w, ch), debajo = debajo, abajo = chI) {
                coronaCiber(g); granoMueble(Rect(0f, 0f, w, ch), true)
            }
        }
        if (fondo - ch < 1f) return

        // ---- el cuerpo: la pared de los lados, cada casillero, lo de los huecos, los costados, las tablas, los rótulos y la regleta
        paredLadosCiber(g)
        d.tablas.forEach { t -> filaJuntaCiber(t.top, g.x0, g.x1, g.c(PaletaCiber.CHAPA, Offset(w * 0.5f, t.top), 0.6f)) }
        d.tablas.forEach { t -> filaJuntaCiber(t.bottom, g.ml, g.mr, g.c(PaletaCiber.TABLERO, Offset(w * 0.5f, t.bottom), 0.5f)) }
        columnaJuntaCiber(g.x0, ch, fondo, g.c(PaletaCiber.CHAPA, Offset(g.x0, (ch + fondo) / 2), 0.6f))
        columnaJuntaCiber(g.x1, ch, fondo, g.c(PaletaCiber.CHAPA, Offset(g.x1, (ch + fondo) / 2), 0.6f))
        d.repisas.forEachIndexed { k, r -> casilleroCiber(k, r, g) }
        d.repisas.forEachIndexed { k, r ->
            val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
            huecoLibreCiber(k, r, oc, g)
        }
        costadosCiber(g, d.tablas)
        d.tablas.forEachIndexed { k, r -> tablaCiber(k, r, k == d.tablas.size - 1, g) }
        d.placas.forEach { p -> rotuloCiber(p, medidor, g) }
        reglaCiber(g)

        // ---- el pie: el armario bajo, el suelo y lo que hay en él
        pieCiber(g)
        granoMueble(Rect(0f, ch, w, h), true)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer) = volumenCiber(v, medidor)
}
