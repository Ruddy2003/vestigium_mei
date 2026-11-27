package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.sqrt

/**
 * Dónde va cada cosa del rincón de la libreta de Navidad, en píxeles, para un lienzo de [w] por [h] y [hojas] casillas escritas. Todo
 * se mide en [U] (un dp, ajustado al alto: el alto de la libreta es fijo y el ancho cambia con el teléfono). La cajonera va algo a
 * la derecha del medio, para que a la izquierda quepan la ventana y los papelitos; a la derecha, la palmatoria y el plato se cortan
 * con el borde si no caben.
 */
internal class SitioLibretaNavidad(val w: Float, val h: Float, u: Float, hojas: Int) {
    val U = u * (h / (MuebleLibretaNavidad.ALTO.value * u)).coerceIn(0.5f, 2f)
    /** El medio de la cajonera. */
    val cx = maxOf(w * 0.53f, 112f * U).coerceAtMost(w - 100f * U)

    // ---------------------------------------------------------------- la pared y la mesa

    /** La cenefa de azulejo verde (arriba y abajo) y, debajo, el azulejo crema hasta la mesa. */
    val cenefa0 = 118f * U
    val cenefa1 = 125f * U
    /** Donde el tablero de la mesa toca la pared, su canto de delante y el faldón. */
    val mesa = 200f * U
    val canto = 222f * U
    val faldon = 230f * U

    // ---------------------------------------------------------------- la ventana (a la izquierda, cortada por el borde)

    /** El hueco de la ventana en la pared: la izquierda queda fuera del cuadro. */
    val ventana = Rect(-14f * U, 8f * U, 76f * U, 104f * U)

    // ---------------------------------------------------------------- la cajonera de adviento

    /** El cuerpo de la cajonera: sus costados y, arriba, la cornisa; abajo, el zócalo y las patas. */
    val ancho = 192f * U
    val izq = cx - ancho / 2
    val der = cx + ancho / 2
    /** El copete recortado con la cartela (el nombre y la cuenta), la cornisa, los cajoncitos, el cajón largo, el zócalo y las patas. */
    val copete = 18f * U
    val cornisa0 = 70f * U
    val cornisa1 = 80f * U
    val cajones0 = 82f * U
    val cajones1 = 178f * U
    val largo0 = 180f * U
    val largo1 = 202f * U
    val zocalo1 = 210f * U
    val pies = 216f * U
    /** El costado que se ve (por la derecha: se mira un poco desde ese lado). */
    val fondo = 9f * U
    /** La cartela del copete, con el nombre y la cuenta. */
    val cartela = Rect(cx - 74f * U, 30f * U, cx + 74f * U, 66f * U)
    /** La rejilla de los 24 cajoncitos: 6 a lo ancho y 4 a lo alto, dentro del marco. */
    val rejilla = Rect(izq + 9f * U, cajones0, der - 9f * U, cajones1)

    /** Cuánto engorda: de 0 (una casilla) a 1 (240 o más); rápido al principio y cada vez más despacio. */
    val lleno = sqrt((hojas.coerceIn(1, 240) - 1) / 239f)

    // ---------------------------------------------------------------- lo de la mesa

    /** La palmatoria (el medio de su platillo) y su llama. */
    val palmatoria = Offset(minOf(der + 34f * U, w - 20f * U), 212f * U)
    /** El plato de las mandarinas. */
    val plato = Offset(minOf(der + 70f * U, w + 10f * U), 216f * U)
    /** Los papelitos con el lápiz, a la izquierda. */
    val papeles = Offset(maxOf(izq - 52f * U, 34f * U), 212f * U)
}

/** Los colores propios de la cajonera: el verde de su pintura, su luz y su sombra (lo demás es de [PaletaNavidad]). */
internal object PaletaLibretaNavidad {
    val VERDE = androidx.compose.ui.graphics.Color(0xFF3E6A4B)
    val VERDE_LUZ = androidx.compose.ui.graphics.Color(0xFF5F8D68)
    val VERDE_OSC = androidx.compose.ui.graphics.Color(0xFF24432F)
}

/**
 * La libreta de momentos de Navidad: el calendario de adviento de la casa, en otro rincón de la misma cocina que la biblioteca y el
 * Ideario: la mesa de pino bajo la ventana. Es una cajonera de sobremesa hecha a mano, de pino pintado de verde con los filetes
 * crema (la misma mano que el aparador, con los colores al revés), con su copete recortado (en la cartela, el nombre y la cuenta), su cornisa, 24
 * cajoncitos numerados en orden revuelto, cada uno con su tirador de porcelana, y abajo el cajón largo, el zócalo y las patas.
 * Cada día que se apunta un momento se abre un cajoncito y se guarda dentro el papelito doblado; pasadas las 24, los papelitos
 * desbordan y se atan en fajos sobre la mesa. En la mesa, los papelitos en blanco con el lápiz (donde se apunta), la palmatoria
 * con su vela y el plato de mandarinas; en la pared, la ventana con su escarcha y la nieve, y la guirnalda de rodajas de naranja
 * seca que cruza de lado a lado.
 *
 * De día, el sol de la ventana deja la mancha de sus vidrios en la pared y en el copete; de noche arde la vela (su charco enciende
 * el costado de la cajonera, la mesa y la pared de ese lado) y por la izquierda entra la luz fría de la nieve, que deja su filo en
 * los cantos que la miran; lo demás se hunde en el verde azulado de la cocina a oscuras. Lo fijo (la pared, la ventana, la mesa,
 * lo de encima y el cuerpo de la cajonera) se pinta una vez por tamaño y modo; los cajoncitos, los papelitos y los rótulos, en
 * cada dibujo. La paleta y la luz son las de la biblioteca ([PaletaNavidad], [AireNavidad]).
 */
internal object MuebleLibretaNavidad {
    val ALTO: Dp = 264.dp

    /** Dónde arde la vela de la palmatoria. */
    fun llama(s: SitioLibretaNavidad): Offset = Offset(s.palmatoria.x + 0.6f * s.U, s.palmatoria.y - 70f * s.U)

    /** Cuánta luz de la vela le llega a [p] (1 junto a la llama, poca lejos); de día, toda. */
    fun cerca(p: Offset, s: SitioLibretaNavidad, a: AireNavidad): Float {
        if (!a.oscuro) return 1f
        val l = llama(s)
        val dx = (p.x - l.x) / s.U; val dy = (p.y - l.y) / s.U
        return (1.75f * exp(-sqrt(dx * dx + dy * dy * 1.15f) / 120f)).coerceIn(0.03f, 1f)
    }

    /** Cuánta luz fría de la nieve de la ventana le llega a [p] de noche (fuerte arriba a la izquierda, se apaga hacia la derecha). */
    fun frio(p: Offset, s: SitioLibretaNavidad): Float {
        val dx = (p.x - s.ventana.right) / s.U; val dy = (p.y - s.ventana.center.y) / s.U
        return (1.05f - sqrt(dx * dx + dy * dy * 0.6f) / 190f).coerceIn(0f, 1f)
    }

    /**
     * La mancha de la ventana en el plano de la pared, en píxeles: de día, el sol bajo de la tarde, que cae entre la ventana y la
     * cajonera y alcanza el copete; de noche no hay mancha (manda la vela) y la nieve solo deja su filo en los cantos.
     */
    fun mancha(s: SitioLibretaNavidad, a: AireNavidad): ManchaNavidad? {
        if (a.oscuro) return null
        val U = s.U; val x = s.ventana.right
        return ManchaNavidad(floatArrayOf(x + 6f * U, 14f * U, x + 66f * U, 24f * U, x + 92f * U, 112f * U, x + 30f * U, 104f * U), PaletaNavidad.SOL, 0.85f)
    }

    private var clave = ""
    private var imagen: ImageBitmap? = null

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val s = SitioLibretaNavidad(w, h, density, e.hojas)
        val a = AireNavidad(oscuro, false)
        val ac = AireNavidad(oscuro, false, cuerpo = true)
        clipRect(0f, 0f, w, h) {
            // La pared, la ventana, la mesa, lo de encima y el cuerpo de la cajonera: una imagen por tamaño y modo.
            val k = "${w.toInt()}:${h.toInt()}:$oscuro:$density"
            var img = imagen
            if (img == null || clave != k) {
                val nueva = ImageBitmap(ceil(w).toInt().coerceAtLeast(1), ceil(h).toInt().coerceAtLeast(1))
                CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(nueva.width.toFloat(), nueva.height.toFloat())) {
                    with(RinconLibretaNavidad) { rincon(s, a, ac) }
                }
                imagen = nueva; clave = k; img = nueva
            }
            drawImage(img)
            // Los cajoncitos, los papelitos, los fajos y los rótulos, en vivo.
            with(CajoneraLibretaNavidad) { cajonera(e, s, ac, medidor) }
            granoMueble(Rect(0f, 0f, w, h), oscuro, 0.7f)
        }
    }
}
