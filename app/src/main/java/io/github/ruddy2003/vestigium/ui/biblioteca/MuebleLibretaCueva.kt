package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.sqrt

/**
 * La libreta de momentos de Prehistoria (la usa `EstanteCueva`, en `BibliotecaAntiguedad.kt`): el rincón de la misma cueva del
 * Ideario y del estante, junto a la boca. A la izquierda, el labio de la boca enmarca un trozo de afuera (el valle con su
 * bruma de día; de noche, el cielo con la luna). Contra la pared de caliza, colgada de un candil de asta clavado en una grieta
 * y asentada en la repisa natural de la roca, la bolsa de piel donde se guardan las pieles pintadas de cada día: un zurrón
 * grande, de cuero curtido, con el cuello de pelo de reno fruncido por su correa y las pieles enrolladas que asoman por la boca
 * (más cuantas más hay). En la panza, un parche de piel raspada cosido con tendón, con el nombre pintado con ocre y la cuenta
 * con carbón. En la repisa, a su derecha, la piel de hoy, extendida y a medio pintar, la piedra de mezclar con sus pegotes de
 * ocre y el pincel de pelo; en la pared, la tea de pino clavada en una grieta, cruzada.
 *
 * De día manda la luz de la boca, que pinta la pared, la bolsa y la repisa con una mancha de borde neto, quebrada en cada
 * plano. De noche arde la tea (su charco enciende la bolsa por la derecha, la piel extendida y la pared) y por la boca entra la
 * luna (una mancha fría a la izquierda); lo lejano se hunde en el pardo violáceo de la cueva. Al abrirse, la correa se afloja,
 * la boca se abre y las pieles suben.
 *
 * Lo que no cambia (la pared, la boca, la repisa, la tea, lo de la repisa) se pinta una vez por tamaño y modo en una imagen
 * ([CapaLibretaCueva]); la bolsa, que engorda con las hojas y se abre, en cada dibujo. El rincón está en
 * `MuebleLibretaCuevaRincon.kt`, la bolsa en `MuebleLibretaCuevaBolsa.kt` y el índice en `MuebleLibretaCuevaIndice.kt`.
 */
internal object MuebleLibretaCueva {
    val ALTO: Dp = 250.dp

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w < 8f || h < 8f) return
        val g = MarcoLibretaCueva(w, h, AireCueva(oscuro, false), e.hojas)
        CapaLibretaCueva.pegar(this, if (oscuro) 1 else 0, "${w.toInt()}|${h.toInt()}|$oscuro|$density|${g.cx.toInt()}") {
            with(RinconLibretaCueva) { rincon(g) }
            granoMueble(Rect(0f, 0f, w, h), oscuro)
        }
        with(BolsaLibretaCueva) { bolsa(g, e, medidor) }
    }
}

/**
 * Las medidas del rincón, en píxeles, y su luz. Todo va en altos del lienzo ([h]), que es fijo; el ancho [w] cambia con el
 * teléfono y el rincón se acomoda: la boca a la izquierda, la bolsa en medio, la tea y la piel extendida a la derecha.
 */
internal class MarcoLibretaCueva(val w: Float, val h: Float, val a: AireCueva, hojas: Int) {
    /** Cuánto ha engordado la bolsa (0 a 1), con la raíz de las hojas. */
    val gordo: Float = (sqrt(hojas.coerceAtLeast(0).toFloat()) / 12f).coerceIn(0f, 1f)
    /** El medio ancho de la panza de la bolsa (no baja de lo que pide el parche con el nombre). */
    val panza: Float = h * (0.23f + 0.07f * gordo)
    /** Donde el labio de la boca toca la repisa: lo de su izquierda es afuera. */
    val boca: Float = (w * 0.2f).coerceIn(h * 0.26f, h * 0.4f)
    /** La cara de arriba de la repisa (donde pisa la bolsa), su canto y el suelo. */
    val repisa: Float = h * 0.755f
    val canto: Float = h * 0.785f
    val suelo: Float = h * 0.885f
    /** El eje de la bolsa: en medio, con sitio a la izquierda para la boca y a la derecha para la tea y la piel de hoy. */
    val cx: Float = (w * 0.5f + h * 0.04f).coerceIn(boca + h * 0.31f, maxOf(boca + h * 0.31f, w - h * 0.58f))
    /** El candil de asta de donde cuelga la bolsa. */
    val candil: Offset = Offset(cx + h * 0.03f, h * 0.085f)
    /** La llama de la tea, clavada en la pared a la derecha. */
    val tea: Offset = Offset(minOf(w - h * 0.1f, cx + h * 0.46f), h * 0.25f)

    /** La mancha de la boca (el sol de día, la luna de noche), en altos: centro, semiejes, giro y semilla. */
    private val mx = boca / h + if (a.oscuro) 0.2f else 0.28f
    private val my = if (a.oscuro) 0.52f else 0.44f
    private val ra = if (a.oscuro) 0.44f else 0.62f
    private val rb = if (a.oscuro) 0.2f else 0.3f
    private val giro = if (a.oscuro) 0.62f else 0.55f
    private val sem = if (a.oscuro) 19 else 7

    /**
     * La luz de un punto, a [delante] altos de la pared (lo que está delante recibe la mancha corrida hacia la boca: así se
     * quiebra en cada plano). Con [mancha] se fuerza cuánto le da la mancha: 0 para pintar lo de fuera y 1 para lo de dentro.
     */
    fun luz(p: Offset, delante: Float = 0f, mancha: Float? = null): LuzCueva {
        val x = p.x / h; val y = p.y / h
        val enMancha = mancha ?: manchaCueva(x + delante * 0.9f, y + delante * 0.5f, mx, my, ra, rb, giro, sem)
        // La tea: un charco corto, que enciende lo cercano y deja hundirse lo lejano.
        val d = (p - tea).getDistance() / h
        val k = if (a.oscuro) (2.3f * exp(-3.1f * d)).coerceAtMost(1f) else 0f
        val tinte = mezcla(PaletaCueva.LLAMA, PaletaCueva.FUEGO, 0.35f)
        val amb = (1f - 0.32f * ((p.x - boca) / h).coerceAtLeast(0f) - 0.06f * y).coerceIn(0.55f, 1f)
        return if (a.oscuro) a.en(k, tinte, 0f, enMancha * 0.5f, amb) else a.en(0f, tinte, enMancha, 0f, amb)
    }

    /** El contorno de la mancha de la boca para lo que está a [delante] altos de la pared, en píxeles. */
    fun mancha(delante: Float): Path = caminoManchaCueva(h, mx, my, ra, rb, giro, sem, -delante * 0.9f, -delante * 0.5f)

    /** De qué lado le llega la luz a algo en [x]: de día, de la boca (izquierda); de noche, de la tea si está más cerca. */
    fun hacia(x: Float): Float = if (a.oscuro && x > (boca + tea.x) * 0.5f) 1f else -1f
}

/**
 * Pinta [pieza] dos veces: con la mancha de la boca apagada y, recortada a la mancha para lo que está a [delante] altos de la
 * pared, encendida (el sol de día, la luna de noche). Así la luz pinta lo que toca, con borde neto.
 */
internal fun DrawScope.conManchaLibretaCueva(g: MarcoLibretaCueva, delante: Float, pieza: DrawScope.(Float) -> Unit) {
    pieza(0f)
    val m = g.mancha(delante)
    val b = m.getBounds()
    clipRect(b.left, b.top, b.right, b.bottom) { clipPath(m) { pieza(1f) } }
}

/**
 * Lo fijo de la libreta, pintado una sola vez por tamaño y modo en una imagen que luego se pega (una ranura por modo). La imagen
 * cubre el lienzo entero: unos 2.6 MB a 1080 px de ancho.
 */
internal object CapaLibretaCueva {
    private val claves = arrayOfNulls<String>(2)
    private val imagenes = arrayOfNulls<ImageBitmap>(2)

    fun pegar(ds: DrawScope, ranura: Int, clave: String, pintar: DrawScope.() -> Unit) {
        val w = ceil(ds.size.width).toInt(); val h = ceil(ds.size.height).toInt()
        if (w < 1 || h < 1 || ranura !in 0..1) return
        val img = imagenes[ranura].takeIf { claves[ranura] == clave } ?: ImageBitmap(w, h).also { nueva ->
            CanvasDrawScope().draw(Density(ds.density, ds.fontScale), ds.layoutDirection, Canvas(nueva), Size(w.toFloat(), h.toFloat())) {
                clipRect(0f, 0f, ds.size.width, ds.size.height) { pintar() }
            }
            imagenes[ranura] = nueva; claves[ranura] = clave
        }
        ds.drawImage(img, IntOffset.Zero, IntSize(img.width, img.height), dstOffset = IntOffset.Zero, dstSize = IntSize(img.width, img.height))
    }
}
