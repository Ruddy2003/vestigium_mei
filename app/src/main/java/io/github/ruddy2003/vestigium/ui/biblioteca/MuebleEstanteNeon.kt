package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * La paleta del cuarto de arriba del puesto (Ciudad neón): el azulejo verde jade de la pared, con su junta y la cenefa granate;
 * la laca bermellón de la alacena, su negro, la madera que asoma donde saltó y el latón; el verde petróleo de dentro de los
 * casilleros; el aluminio, el hierro, el papel y la tinta; el linóleo del suelo; y las luces del barrio (el magenta y el ámbar del
 * letrero de enfrente, el cian del acuario, el ámbar de la lámpara de pinza, el naranja de la estufa y, en los Sueños, el verde
 * amarillento de la farola de sodio). Dos temperaturas: lo cálido es la laca, el latón y las luces; lo frío, el jade, el petróleo,
 * el acuario y el sodio.
 */
internal object PaletaNeon {
    /** Las luces: el letrero de enfrente, el acuario, la lámpara de pinza, la estufa y, en los Sueños, la farola de sodio. */
    val MAGENTA = Color(0xFFFF3D9A)
    /** La luz que el letrero de enfrente echa en el cuarto: magenta con un poco de ámbar. */
    val ROSA = Color(0xFFFF4A92)
    val AMBAR = Color(0xFFFFB238)
    val CIAN = Color(0xFF52E4EA)
    val NARANJA = Color(0xFFFF7B35)
    val SODIO = Color(0xFFCFE65C)
    /** La luz de la farola ya dentro del cuarto, sobre las cosas: un amarillo de sodio más cálido (el jade bajo ella, oliva). */
    val SODIO_CUARTO = Color(0xFFE2D45A)
    val AZUL_ACUARIO = Color(0xFF5A84FF)
    val BRASA = Color(0xFFE8472C)
    /** El azulejo jade de la pared: su cara, su luz, su hondo y la junta; la cenefa granate y el cemento que asoma. */
    val JADE = Color(0xFF5DA894)
    val JADE_LUZ = Color(0xFF9FD8C4)
    val JADE_OSC = Color(0xFF34705F)
    val JUNTA = Color(0xFF26332F)
    val GRANATE = Color(0xFF962A3A)
    val CEMENTO = Color(0xFF7B777B)
    /** La laca bermellón de la alacena, su luz y su sombra; la laca negra de las molduras; la madera que asoma y el latón. */
    val LACA = Color(0xFFBC2A2C)
    val LACA_LUZ = Color(0xFFE85E44)
    val LACA_OSC = Color(0xFF761822)
    val NEGRO_LACA = Color(0xFF221619)
    val MADERA = Color(0xFFA2714A)
    val LATON = Color(0xFFD9AC4E)
    val LATON_OSC = Color(0xFF8A6427)
    /** El verde petróleo de dentro de los casilleros y su luz. */
    val PETROLEO = Color(0xFF1F5A56)
    val PETROLEO_LUZ = Color(0xFF34847B)
    /** El aluminio, el hierro, el plástico gris, el papel de las etiquetas, la tinta, el hueso y el cartón. */
    val ALUMINIO = Color(0xFFBEC1CB)
    val HIERRO = Color(0xFF34333C)
    val PLASTICO = Color(0xFF8E8C99)
    val PAPEL = Color(0xFFD3C9CF)
    val TINTA = Color(0xFF1A1320)
    val HUESO = Color(0xFFEDE4D8)
    val CARTON = Color(0xFFAA855A)
    /** El linóleo del suelo, a cuadros: el crema y el verde gastados. */
    val LINO = Color(0xFFC9B892)
    val LINO_VERDE = Color(0xFF4C7F76)
    /** Lo hondo del cuarto (el oscuro con color: un verde petróleo casi negro) y la sombra. */
    val HONDO = Color(0xFF081719)
    val SOMBRA = Color(0xFF040A0E)

    val LETRA: FontFamily = Letra.letrero
}

// ---------------------------------------------------------------- la luz del cuarto

/**
 * La luz del cuarto en un estado: en el diario ([noche] falso), el letrero de enfrente por la ventana (magenta y ámbar), el
 * acuario (cian), la lámpara de pinza (ámbar) y la estufa (naranja); en los Sueños, la farola de sodio por la ventana (manda), el
 * acuario en su luz de noche (azul y baja), la lámpara apagada y la estufa al mínimo. El tema es solo oscuro: el modo claro no
 * cambia nada.
 */
internal class AireNeon(val noche: Boolean, val pano: Int = PANO_SUAVE) {
    private val p = PaletaNeon
    /**
     * El color de cada luz: la ventana, el acuario, la lámpara y la estufa. En los Sueños, la farola es el sodio amarillo verdoso en
     * el paño que cae sobre el mueble y un sodio más cálido en el resplandor de la pared (el jade, oliva y no lima).
     */
    val colores: Array<Color> = if (noche) arrayOf(if (pano == PANO_NO) p.SODIO_CUARTO else p.SODIO, p.AZUL_ACUARIO, p.AMBAR, p.BRASA)
        else arrayOf(p.ROSA, p.CIAN, p.AMBAR, p.NARANJA)
    /** La fuerza de cada una. En los Sueños el acuario, solo con su luz de noche, pesa más en lo que tiene cerca. */
    val fuerzas: FloatArray = if (noche) floatArrayOf(0.85f, 0.8f, 0f, 0.4f) else floatArrayOf(1.15f, 0.58f, 1.1f, 1f)
    /** Si la lámpara de pinza está encendida. */
    val lampara: Boolean get() = !noche

    /** Lo hondo de un color: oscuro pero de su mismo tono, apenas hacia el petróleo del cuarto (en los Sueños, más hundido). */
    fun hondo(c: Color): Color {
        val n = p.HONDO
        val (a, b) = if (noche) 0.14f to 0.5f else 0.2f to 0.62f
        return saturarNeon(Color(c.red * a + n.red * b, c.green * a + n.green * b, c.blue * a + n.blue * b, c.alpha), 1.35f)
    }

    /** Cuánto de su color conserva algo con [k] de luz: sube deprisa cerca de la luz y cae a casi nada lejos. */
    private fun cuanto(k: Float): Float = (1.35f * k.coerceIn(0f, 1.3f).pow(1.45f)).coerceAtMost(0.95f)

    /** Un color con la luz [k] de color [l]: teñido por ella (la luz multiplica el color), encendido cerca y hundido lejos. */
    fun luz(c: Color, k: Float, l: Color): Color {
        val kk = k.coerceIn(0f, 1.3f)
        // La luz, algo aclarada, multiplica el color (como en una foto de neón: lo que está bajo el magenta se vuelve magenta), y
        // cerca de ella además lo enciende con su color. Se mezcla a mano, en RGB, para no lavar el tono de los complementarios.
        val lr = 0.08f + 0.92f * l.red; val lg = 0.08f + 0.92f * l.green; val lb = 0.08f + 0.92f * l.blue
        val v = if (noche) {
            // En los Sueños la luz no enciende: tiñe. El color bajo el sodio se va al oliva y al ocre, y bajo el acuario, al azul,
            // sin subir de valor (que no se lave).
            val m = Color(c.red * (0.12f + 0.88f * lr), c.green * (0.12f + 0.88f * lg), c.blue * (0.12f + 0.88f * lb), c.alpha)
            mezcla(m, Color(l.red * 0.8f, l.green * 0.8f, l.blue * 0.8f, c.alpha), 0.45f * min(1f, kk))
        } else {
            val brilla = 0.3f * kk * kk
            Color(min(1f, c.red * (0.12f + 0.88f * lr) + l.red * brilla), min(1f, c.green * (0.12f + 0.88f * lg) + l.green * brilla),
                min(1f, c.blue * (0.12f + 0.88f * lb) + l.blue * brilla), c.alpha)
        }
        return mezcla(hondo(c), v, cuanto(kk))
    }

    /** La sombra: el negro con color del cuarto, nunca negro puro. */
    fun sombra(a: Float): Color = p.SOMBRA.copy(alpha = a.coerceIn(0f, 1f))
    /** El filo de luz en lo que mira a la luz: del color de la luz, aclarado; algo siempre, más cerca de ella. */
    fun filo(a: Float, k: Float, l: Color): Color = mezcla(l, Color.White, 0.42f).copy(alpha = (a * (0.22f + 0.95f * k.coerceIn(0f, 1f))).coerceIn(0f, 1f))

    /**
     * El papel donde va el día: teñido de la luz que le llega (rosa junto a la ventana, ámbar junto a la lámpara, naranja cerca de
     * la estufa) y, lejos de ella, hundido hasta lo justo para que la tinta siga dando 4.5 a 1 (luminancia 0.215): un papel en
     * penumbra del color de su luz, no un blanco.
     */
    fun etiqueta(k: Float, l: Color, base: Color = p.PAPEL): Color {
        val ke = k.coerceIn(0f, 1f).pow(2f)
        val porLuz = Color(base.red * l.red, base.green * l.green, base.blue * l.blue)
        val lejos = conLuminanciaNeon(saturarNeon(mezcla(base, porLuz, 0.5f), 0.8f), ETIQUETA_MINIMA_NEON)
        val cerca = mezcla(base, porLuz, 0.5f)
        val e = mezcla(lejos, cerca, ke)
        return if (luminanciaNeon(e) < ETIQUETA_MINIMA_NEON) conLuminanciaNeon(e, ETIQUETA_MINIMA_NEON) else e
    }
    val tinta: Color get() = p.TINTA
}

/** La luminancia de una etiqueta que con la tinta ([PaletaNeon.TINTA]) da justo 4.5 a 1. */
internal const val ETIQUETA_MINIMA_NEON = 0.215f

private fun linealNeon(x: Float): Float = x.coerceIn(0f, 1f).pow(2.2f)

/** La luminancia relativa de [c] (con la curva del sRGB aproximada a 2.2). */
internal fun luminanciaNeon(c: Color): Float = 0.2126f * linealNeon(c.red) + 0.7152f * linealNeon(c.green) + 0.0722f * linealNeon(c.blue)

/** [c] llevado a la luminancia [l] sin cambiarle el tono (escala la luz lineal de los tres canales). */
internal fun conLuminanciaNeon(c: Color, l: Float): Color {
    val actual = luminanciaNeon(c)
    if (actual < 1e-4f) return Color(l.pow(1f / 2.2f), l.pow(1f / 2.2f), l.pow(1f / 2.2f), c.alpha)
    val f = l / actual
    fun canal(x: Float) = (linealNeon(x) * f).coerceIn(0f, 1f).pow(1f / 2.2f)
    return Color(canal(c.red), canal(c.green), canal(c.blue), c.alpha)
}

/** [c] con su saturación multiplicada por [k] (alrededor de su gris). */
internal fun saturarNeon(c: Color, k: Float): Color {
    val l = 0.3f * c.red + 0.59f * c.green + 0.11f * c.blue
    fun f(x: Float) = (l + (x - l) * k).coerceIn(0f, 1f)
    return Color(f(c.red), f(c.green), f(c.blue), c.alpha)
}

/**
 * Lo que llega de cada luz a un punto ([fx], [fy] en anchos del mueble), en [k] (ventana, acuario, lámpara, estufa): la
 * ventana es una luz grande en la pared (cae con la distancia a su vidrio y baja hacia el cuarto); el acuario, un charco corto
 * alrededor del vidrio; la lámpara de pinza abre su charco hacia la izquierda y abajo; la estufa sube más que se abre.
 */
internal fun lucesNeon(fx: Float, fy: Float, a: AireNeon, lampY: Float, estufaY: Float, k: FloatArray) {
    val m = MuebleEstanteNeon
    // La ventana: la distancia a su vidrio, y lo que cae hacia abajo y a la derecha, al cuarto.
    val dxv = max(0f, max(m.VENT_X0 - fx, fx - m.VENT_X1)); val dyv = max(0f, max(m.VENT_Y0 - fy, fy - m.VENT_Y1))
    val dv = sqrt(dxv * dxv + dyv * dyv * 1.3f)
    if (a.noche) {
        // En los Sueños, el resplandor pegado al vidrio y el paño de la farola, que baja por el mueble (ver [panoNeon]).
        val pano = when (a.pano) { PANO_NO -> 0f; PANO_LLENO -> panoNeon(fx, fy, true); else -> panoNeon(fx, fy, false) }
        k[0] = a.fuerzas[0] * (0.95f * exp(-6.5f * dv) + 1.1f * pano)
    } else {
        // El resplandor en la pared, pegado al vidrio, y el cono que baja hacia el cuarto: se abre y se corre a la derecha.
        val dyb = max(0f, fy - m.VENT_Y1 + 0.04f)
        val eje = (m.VENT_X0 + m.VENT_X1) / 2 + 0.32f * dyb
        val abre = 0.17f + 0.32f * dyb
        val cono = exp(-((fx - eje) / abre) * ((fx - eje) / abre)) * exp(-1.55f * dyb) * (if (fy > m.VENT_Y1 - 0.04f) 1f else 0f)
        k[0] = a.fuerzas[0] * (0.95f * exp(-6.5f * dv) + 1.1f * cono)
    }
    // El acuario (de noche, su luz azul llega algo más lejos).
    val dxa = max(0f, max(m.ACU_X0 - fx, fx - m.ACU_X1)); val dya = max(0f, max(m.ACU_Y0 - fy, fy - m.ACU_Y1))
    k[1] = a.fuerzas[1] * exp((if (a.noche) -6.2f else -9f) * sqrt(dxa * dxa * 1.4f + dya * dya))
    // La lámpara de pinza: ilumina hacia la izquierda y abajo. (Lo que llega, tenue, a la repisa de arriba y a la de abajo se
    // pinta aparte, con su caída: ver `caidasCasilleroNeon` y `filosLucesNeon`.)
    val dxl = fx - m.LAMP_X; val dyl = fy - lampY
    val haciaDentro = if (dxl < 0f) 0.62f else 1.7f
    k[2] = a.fuerzas[2] * 1.1f * exp(-4.4f * sqrt(dxl * dxl * haciaDentro + dyl * dyl * (if (dyl > 0f) 0.95f else 1.5f)))
    // La estufa: sube por los cajones hasta las repisas de abajo y, tenue, por el frente del mueble hasta la cuarta (el aire
    // caliente sube más que se abre). Esa cola es nada junto a la estufa, donde ya manda ella.
    val dxe = fx - m.ESTUFA_X; val dye = (fy - estufaY) * (if (fy < estufaY) 0.66f else 1f)
    val de = sqrt((dxe / 0.8f) * (dxe / 0.8f) + (dye / 1.6f) * (dye / 1.6f))
    k[3] = a.fuerzas[3] * (exp(-4.4f * sqrt(dxe * dxe + dye * dye)) + (if (fy < estufaY) 0.3f * exp(-2f * de) * (1f - exp(-5f * de)) else 0f))
}

/** Cómo entra el paño de la farola en la luz: suave en sus bordes (lo normal), fuera (la pared, detrás) o entero (dentro de su
 * recorte, ver [bandaNeon]). */
internal const val PANO_SUAVE = 1
internal const val PANO_NO = 0
internal const val PANO_LLENO = 2

private fun suaveNeon(a: Float, b: Float, x: Float): Float {
    val t = ((x - a) / (b - a)).coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}

/**
 * El paño de la farola de los Sueños en ([fx], [fy]), de 0 a 1: la luz de sodio entra por la ventana desde arriba y a la
 * izquierda y cae sobre el mueble (no sobre la pared, que está detrás): un paralelogramo que arranca en la cara de arriba de la
 * cornisa, baja por el costado izquierdo y las primeras repisas corriéndose a la derecha y se apaga hacia abajo. [lleno]: sin
 * los bordes (lo que vale dentro, a esa altura).
 */
internal fun panoNeon(fx: Float, fy: Float, lleno: Boolean): Float {
    val m = MuebleEstanteNeon
    if (fy < m.PANO_Y0 - 0.012f || fy > m.PANO_Y1) return 0f
    val f = 1f - suaveNeon(0.64f, m.PANO_Y1, fy)
    if (lleno) return f
    val iz = m.panoIzq(fy); val de = m.panoDer(fy); val b = 0.016f
    return f * suaveNeon(iz - b, iz + b, fx) * (1f - suaveNeon(de - b, de + b, fx)) * suaveNeon(m.PANO_Y0 - 0.012f, m.PANO_Y0 + 0.01f, fy)
}

/** La luz que le toca a un punto: cuánta ([k]), de qué color ([l]) y de qué lado le llega ([hacia]: -1 izquierda, 1 derecha). */
internal class TonoNeon(val a: AireNeon, val k: Float, val l: Color, val hacia: Float) {
    fun c(col: Color, mas: Float = 1f): Color = a.luz(col, k * mas, l)
    fun s(al: Float): Color = a.sombra(al)
    fun b(al: Float, mas: Float = 1f): Color = a.filo(al, k * mas, l)
    val izq: Boolean get() = hacia < 0f
}

/** El tono en ([fx], [fy]) en anchos del mueble. */
internal fun tonoNeon(fx: Float, fy: Float, a: AireNeon, lampY: Float, estufaY: Float): TonoNeon {
    val k = FloatArray(4)
    lucesNeon(fx, fy, a, lampY, estufaY, k)
    var suma = 0f; var r = 0f; var g = 0f; var b = 0f; var lado = 0f
    val m = MuebleEstanteNeon
    val xs = floatArrayOf((m.VENT_X0 + m.VENT_X1) / 2f, (m.ACU_X0 + m.ACU_X1) / 2f, m.LAMP_X, m.ESTUFA_X)
    for (i in 0 until 4) {
        val ki = k[i]; if (ki <= 0f) continue
        val col = a.colores[i]
        suma += ki; r += col.red * ki; g += col.green * ki; b += col.blue * ki
        lado += ki * (if (xs[i] > fx) 1f else -1f)
    }
    if (suma < 1e-4f) return TonoNeon(a, 0f, a.colores[0], -1f)
    return TonoNeon(a, suma.coerceAtMost(1.3f), Color(r / suma, g / suma, b / suma), if (lado >= 0f) 1f else -1f)
}

/**
 * Las medidas del cuarto que comparten las piezas, en píxeles: el ancho [w] y el alto [h] del lienzo, el pie de la corona [ch],
 * el borde de dentro de los costados ([x0], [x1]), el pie de la última tabla ([fondo]), una repisa con su tabla ([fila]) y
 * cuántas repisas hay ([n]). Y el tono en cada punto.
 */
internal class MarcoNeon(val w: Float, val h: Float, val ch: Float, val x0: Float, val x1: Float, val fondo: Float, val fila: Float,
                         val n: Int, val a: AireNeon) {
    val m = MuebleEstanteNeon
    /** Donde se apoya lo de encima: la cara de arriba de la cornisa, de atrás a adelante. */
    val apoyo: Float get() = ch - w * m.CORNISA
    val apoyoAtras: Float get() = apoyo - w * 0.022f
    /** Los bordes del mueble (la pared asoma fuera). */
    val ml: Float get() = w * m.BORDE
    val mr: Float get() = w - w * m.BORDE
    /** El cuerpo bajo: los cajones bajo la última tabla, el zócalo, el rincón con el suelo y donde pisan las patas. */
    val cajones: Float get() = fondo + w * 0.1f
    val zocalo: Float get() = fondo + w * 0.13f
    val suelo: Float get() = fondo + w * 0.145f
    val pisan: Float get() = fondo + w * 0.165f
    /** La lámpara de pinza y la estufa, en anchos. */
    val lampY: Float get() = (ch + fila * (filaLamparaNeon(n) + 0.04f)) / w + m.LAMP_DY
    val estufaY: Float get() = fondo / w + m.ESTUFA_DY

    fun t(p: Offset): TonoNeon = tonoNeon(p.x / w, p.y / w, a, lampY, estufaY)
    fun t(x: Float, y: Float): TonoNeon = tonoNeon(x / w, y / w, a, lampY, estufaY)
    fun c(col: Color, p: Offset, mas: Float = 1f): Color = t(p).c(col, mas)

    // ---- el paño de la farola (solo en los Sueños): el mismo marco sin él (la pared; el fondo de lo que lo lleva) y con él
    // entero (lo de dentro de su recorte), y su recorte en píxeles.
    val sinPano: MarcoNeon by lazy { if (a.pano == PANO_NO) this else MarcoNeon(w, h, ch, x0, x1, fondo, fila, n, AireNeon(a.noche, PANO_NO)) }
    val conPano: MarcoNeon by lazy { MarcoNeon(w, h, ch, x0, x1, fondo, fila, n, AireNeon(a.noche, PANO_LLENO)) }
    val panoRecorte: Path by lazy {
        val y0 = m.PANO_Y0; val y1 = m.PANO_Y1
        poligonoNeon(w * m.panoIzq(y0), w * y0, w * m.panoDer(y0), w * y0, w * m.panoDer(y1), w * y1, w * m.panoIzq(y1), w * y1)
    }
    /** Si el rectángulo de [xa] a [xb] y de [ya] a [yb] (en píxeles) toca el paño. */
    fun tocaPano(xa: Float, xb: Float, ya: Float, yb: Float): Boolean {
        if (!a.noche || a.pano != PANO_SUAVE || yb <= w * m.PANO_Y0 || ya >= w * m.PANO_Y1) return false
        val fa = maxOf(ya / w, m.PANO_Y0); val fb = minOf(yb / w, m.PANO_Y1)
        return xb > w * m.panoIzq(fa) && xa < w * m.panoDer(fb)
    }
}

/** Para un volumen: dónde cae en anchos del mueble ([repisa] desde arriba y su centro [centro] de 0 a 1). */
internal fun lugarVolumenNeon(repisa: Int, centro: Float, alto: Float = 0.55f): Offset {
    val m = MuebleEstanteNeon
    return Offset(m.LADO + centro * (1f - 2 * m.LADO), m.CORONA + (repisa + alto) * m.FILA / m.ANCHO_NOMINAL)
}

/** La altura de la bombilla y de la estufa, para un volumen que no sabe cuánto mide el estante (lo supone de un teléfono). */
internal fun lampNominalNeon(repisas: Int): Float {
    val m = MuebleEstanteNeon
    return m.CORONA + (filaLamparaNeon(repisas) + 0.04f) * m.FILA / m.ANCHO_NOMINAL + m.LAMP_DY
}
internal fun estufaNominalNeon(repisas: Int): Float {
    val m = MuebleEstanteNeon
    return m.CORONA + repisas * m.FILA / m.ANCHO_NOMINAL + m.ESTUFA_DY
}

// ---------------------------------------------------------------- lo fijo pintado una vez

/**
 * La corona pintada una vez por tamaño y estado, en dos ranuras (el diario y los Sueños). Pegar una imagen cuesta mucho menos
 * que repetir la pared, la ventana y el bodegón en cada dibujo. Unos 2.3 MB por ranura en un teléfono de 1080 px.
 */
internal object CapaNeon {
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

/** La columna de la junta en [x], de [y0] a [y1], de [c], debajo de las dos piezas que se tocan ahí; si [x] es entero, nada. */
internal fun DrawScope.columnaJuntaNeon(x: Float, y0: Float, y1: Float, c: Color) {
    val f = floor(x)
    if (f == x || y1 <= y0) return
    drawRect(c, Offset(f, y0), Size(1f, y1 - y0))
}

/** La fila de la junta en [y], de [x0] a [x1], de [c], debajo de las dos piezas que se tocan ahí; si [y] es entero, nada. */
internal fun DrawScope.filaJuntaNeon(y: Float, x0: Float, x1: Float, c: Color) {
    val f = floor(y)
    if (f == y || x1 <= x0) return
    drawRect(c, Offset(x0, f), Size(x1 - x0, 1f))
}

// ---------------------------------------------------------------- ayudantes de forma

internal fun poligonoNeon(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** La sombra de lo que pisa: un óvalo corrido hacia donde no da la luz. */
internal fun DrawScope.contactoNeon(centro: Offset, ancho: Float, alto: Float, t: TonoNeon, fuerza: Float = 0.5f) =
    drawOval(t.s(fuerza), Offset(centro.x - ancho * (if (t.izq) 0.4f else 0.6f), centro.y - alto / 2), Size(ancho, alto))

/** Un tornillo o un clavo de cabeza redonda: su sombra, la cabeza y el punto de luz. */
internal fun DrawScope.clavoNeon(p: Offset, r: Float, t: TonoNeon, metal: Color = PaletaNeon.LATON) {
    drawCircle(t.s(0.5f), r * 1.05f, p + Offset(r * (if (t.izq) 0.45f else -0.45f), r * 0.5f))
    drawCircle(t.c(metal), r, p)
    drawCircle(t.b(0.85f), r * 0.4f, p + Offset(r * (if (t.izq) -0.3f else 0.3f), -r * 0.3f))
}

/**
 * El lado de luz y el de sombra de un cuerpo de pie dentro de [r] (ir dentro de su recorte): la franja de luz del lado que mira a
 * la luz y la de sombra del otro, con su filo.
 */
internal fun DrawScope.planosNeon(r: Rect, t: TonoNeon, fuerza: Float = 1f) {
    val luzIzq = t.izq
    val xl = if (luzIzq) r.left else r.right - r.width * 0.26f
    drawRect(t.b(0.2f * fuerza), Offset(xl, r.top), Size(r.width * 0.26f, r.height))
    drawRect(t.b(0.4f * fuerza), Offset(if (luzIzq) r.left + r.width * 0.07f else r.right - r.width * 0.13f, r.top), Size(r.width * 0.06f, r.height))
    val xs = if (luzIzq) r.right - r.width * 0.36f else r.left
    drawRect(t.s(0.3f * fuerza), Offset(xs, r.top), Size(r.width * 0.36f, r.height))
    drawRect(t.s(0.25f * fuerza), Offset(if (luzIzq) r.right - r.width * 0.13f else r.left, r.top), Size(r.width * 0.13f, r.height))
}

/**
 * El dibujo del estante del diario de Ciudad neón (lo usa `EstanteNeon`, en `BibliotecaFuturo.kt`): el cuarto de arriba del
 * puesto, en el nivel −3, siempre de noche. Es una alacena de madera lacada en rojo bermellón, rescatada del restaurante de
 * enfrente cuando cerró: la cornisa de laca negra con su moldura, la fila de clavos de latón y el friso calado; los costados
 * gruesos con su media caña y las cantoneras de latón; las pletinas de las bisagras de las puertas que ya no están; las tablas
 * gruesas con el canto negro y el filete dorado; el fondo de los casilleros lacado de verde petróleo; abajo, dos cajones y el
 * zócalo sobre patas. La ciudad le ha puesto una regleta, cables con bridas y la lámpara de pinza. Contra la pared de azulejo
 * jade con su cenefa y la ventana al callejón, con el letrero de enfrente. Encima, el acuario (manda), la radio, la planta de
 * potos y lo colgado (el paraguas, el reloj sin cifras). Al pie, el linóleo con el charco, la estufa de resistencias, las botas
 * y la caja de botellas. La pared, la ventana y lo colgado están en `MuebleEstanteNeonMuro.kt`; la cornisa y el bodegón, en
 * `MuebleEstanteNeonCorona.kt`; los casilleros, costados, tablas y la lámpara, en `MuebleEstanteNeonMueble.kt`; lo de los
 * huecos, en `MuebleEstanteNeonHuecos.kt`; el pie, en `MuebleEstanteNeonPie.kt`; las entradas, en
 * `MuebleEstanteNeonVolumenes.kt`.
 */
internal object MuebleEstanteNeon {
    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteNeon` las lee al dibujar)

    val CORONA = 0.6f
    val BASE = 0.34f
    val LADO = 0.075f
    /** La franja de pared que asoma a cada lado de la alacena, en anchos. */
    val BORDE = 0.026f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 18.dp
    val SEPARA: Dp = 3.dp
    /** La cornisa, en anchos: de donde pisan las cosas al arranque de las repisas. */
    val CORNISA = 0.11f

    fun ancho(palabras: Int): Dp = (31 + (palabras / 40).coerceAtMost(16)).dp

    /** Cuál de los tres es una entrada: 0, el cartucho de datos; 1, la lata de té; 2, el saco de cordón. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla, 73)
        return if (x < 0.37f) 0 else if (x < 0.69f) 1 else 2
    }

    /** El cartucho, el más alto; la lata, con su tapa; el saco, más bajo y asentado. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.86f + 0.07f * azar(semilla, 1)
        1 -> 0.82f + 0.08f * azar(semilla, 1)
        else -> 0.79f + 0.06f * azar(semilla, 1) + 0.03f * (palabras / 800f).coerceIn(0f, 1f)
    }

    // ---------------------------------------------------------------- la luz (en anchos del mueble, desde arriba a la izquierda)

    /** El vidrio de la ventana. */
    val VENT_X0 = 0.045f
    val VENT_X1 = 0.335f
    val VENT_Y0 = 0.04f
    val VENT_Y1 = 0.33f
    /** El vidrio del acuario. */
    val ACU_X0 = 0.47f
    val ACU_X1 = 0.84f
    val ACU_Y0 = 0.27f
    val ACU_Y1 = 0.475f
    /** La bombilla de la lámpara de pinza: su x, la fracción de las repisas donde va y lo que baja de la pinza, en anchos. */
    val LAMP_X = 0.865f
    val LAMP_F = 0.44f
    val LAMP_DY = 0.05f
    /** La estufa: su x y su altura bajo la última tabla, en anchos. */
    val ESTUFA_X = 0.15f
    val ESTUFA_DY = 0.2f
    /**
     * El paño de la farola en los Sueños (ver [panoNeon]): arriba, en la cara de la cornisa, y abajo, donde ya se apagó; sus
     * bordes de la izquierda y de la derecha en cada altura (el de la izquierda apenas se corre, para bajar por el costado).
     */
    val PANO_Y0 = 0.455f
    val PANO_Y1 = 1.62f
    fun panoIzq(fy: Float): Float = -0.02f + 0.1f * (fy - PANO_Y0)
    fun panoDer(fy: Float): Float = 0.33f + 0.26f * (fy - PANO_Y0)
    /** La sombra de la reja en el paño: los barrotes (en la fracción de su ancho) y los travesaños (en anchos del mueble). */
    val PANO_BARROTES = floatArrayOf(0.2f, 0.4f, 0.6f, 0.8f)
    val PANO_TRAVESANOS = floatArrayOf(0.69f, 1.06f)
    /** El ancho con que un volumen pasa su repisa a anchos del mueble: el de un teléfono. */
    val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    val FILA = 130f

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireNeon(noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * u
        val fondo = if (n > 0) d.tablas.last().bottom else ch
        val g = MarcoNeon(w, h, ch, d.izquierda.right, d.derecha.left, fondo, fila, n, a)

        // ---- lo fijo de arriba (la pared, la ventana, lo colgado, la cornisa y el bodegón), pintado una vez por tamaño y estado.
        // Si el borde de la corona cae a medio píxel, la imagen llega al píxel entero de abajo y, debajo de esa fila, va la
        // corona corrida un píxel: así no asoma el fondo entre la corona y el cuerpo.
        val clave = "${w.toInt()}|${ch.toInt()}|${fila.toInt()}|$n|${fondo.toInt()}|$noche|$u"
        val chI = ceil(ch)
        with(CapaNeon) {
            val debajo: (DrawScope.() -> Unit)? = if (chI > ch && chI >= 2f) {
                { clipRect(0f, chI - 1f, w, chI) { translate(0f, 1f) { coronaNeon(g); granoMueble(Rect(0f, 0f, w, ch), true) } } }
            } else null
            pegar(if (noche) 1 else 0, clave, Rect(0f, 0f, w, ch), debajo = debajo, abajo = chI) {
                coronaNeon(g); granoMueble(Rect(0f, 0f, w, ch), true)
            }
        }
        if (fondo - ch < 1f) return

        // ---- el cuerpo: la pared de los lados, cada casillero, los costados, las tablas, los rótulos y la lámpara
        paredLadosNeon(g)
        d.tablas.forEach { t -> filaJuntaNeon(t.top, g.x0, g.x1, g.c(PaletaNeon.NEGRO_LACA, Offset(w * 0.5f, t.top))) }
        // Las juntas de cada casillero con la tabla de arriba y la de abajo, y con los costados, que pueden caer a medio píxel.
        d.tablas.forEach { t -> filaJuntaNeon(t.bottom, g.ml, g.mr, g.c(PaletaNeon.LACA_OSC, Offset(w * 0.5f, t.bottom))) }
        columnaJuntaNeon(g.x0, ch, fondo, g.c(PaletaNeon.LACA_OSC, Offset(g.x0, (ch + fondo) / 2)))
        columnaJuntaNeon(g.x1, ch, fondo, g.c(PaletaNeon.LACA_OSC, Offset(g.x1, (ch + fondo) / 2)))
        d.repisas.forEachIndexed { k, r -> casilleroNeon(k, r, g) }
        d.repisas.forEachIndexed { k, r ->
            val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
            huecoLibreNeon(k, r, oc, g)
        }
        costadosNeon(g, d.tablas)
        d.tablas.forEachIndexed { k, r -> tablaNeon(k, r, k == d.tablas.size - 1, g) }
        filosLucesNeon(g, d.tablas)
        d.placas.forEach { p -> rotuloNeon(p, medidor, g) }
        lamparaPinzaNeon(g)
        sombrasPanoNeon(g, ch, h)

        // ---- el pie: el cuerpo bajo, el suelo y lo que hay en él
        pieNeon(g)
        granoMueble(Rect(0f, ch, w, h), true)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer) = volumenNeon(v, medidor)

}
