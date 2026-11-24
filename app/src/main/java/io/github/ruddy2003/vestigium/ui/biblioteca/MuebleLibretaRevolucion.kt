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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.sqrt

/**
 * Dónde va cada cosa del rincón de la libreta de Revolución, en píxeles, para un lienzo de [w] por [h] y un registro de [hojas]
 * páginas. Todo se mide en [U] (un dp del alto de diseño), así el rincón no se deforma si cambia el ancho: la prensa queda
 * pegada al borde derecho, el pupitre a su izquierda y lo que sobra a la izquierda es pared (con la luz de la ventana).
 */
internal class SitioLibretaRevolucion(val w: Float, val h: Float, u: Float, hojas: Int) {
    val U = u * (h / (MuebleLibretaRevolucion.ALTO.value * u)).coerceIn(0.5f, 2f)
    /** El pie derecho de la prensa, a la derecha. */
    val prensaIzq = w - 40f * U
    /** El pupitre: sus lados y sus alturas (la viga, la repisa de atrás, el reborde que sujeta el registro, el faldón, las patas). */
    val der = prensaIzq - 14f * U
    val pw = minOf(278f * U, der - 10f * U)
    val izq = der - pw
    val viga = 9f * U
    val galeria = 38f * U
    val galeriaAbajo = 45f * U
    val reborde = 203f * U
    val faldon = 210f * U
    val faldonAbajo = 226f * U
    val suelo = 230f * U
    val pata = 257f * U
    val zocalo = 182f * U
    /** El registro: la tapa, de pie en el tablero, y su grueso (de 6 a 24 dp), que asoma corrido abajo a la derecha. */
    val g = (6f + 18f * sqrt(hojas.coerceIn(0, 240) / 240f)) * U
    val gx = g * 0.62f
    val gy = g * 0.4f
    val bw = minOf(146f * U, pw - 80f * U)
    val registro = Rect(izq + maxOf(24f * U, pw * 0.2f), 50f * U, izq + maxOf(24f * U, pw * 0.2f) + bw, reborde - gy)
    /** La vela de sebo en su palmatoria, en la repisa a la derecha, y su llama (de aquí sale la luz de la noche). */
    val vela = Offset(der - 30f * U, galeria)
    val llama = Offset(vela.x, galeria - 19f * U)
    /** El cuerno de tinta con su pluma y la salvadera, en la repisa a la izquierda. */
    val cuerno = Offset(izq + 30f * U, galeria)
    val salvadera = Offset(izq + 56f * U, galeria)
    /** La luz de la ventana de la izquierda (de día, el sol; de noche, la luna): en la pared, en el tablero y en el registro. */
    val pano = PanoLibretaRevolucion(this)
}

/**
 * La luz de la ventana de la izquierda, que entra por una ventana de dos hojas con su travesaño: cuatro paños separados por la
 * sombra del parteluz y del travesaño. Cae en la pared, y en lo que está delante de la pared (el tablero, el registro) cae
 * corrida hacia arriba a la izquierda, tanto más cuanto más adelante está: el borde se quiebra en cada plano.
 */
internal class PanoLibretaRevolucion(s: SitioLibretaRevolucion) {
    private val U = s.U
    /** Las esquinas en la pared: arriba a la izquierda, arriba a la derecha, abajo a la derecha y abajo a la izquierda. */
    private val x0 = s.izq - 46f * U
    private val q = floatArrayOf(x0, 56f * U, x0 + 132f * U, 78f * U, x0 + 132f * U, 176f * U, x0, 154f * U)

    private fun punto(fx: Float, fy: Float, dx: Float, dy: Float): Offset {
        val tx = q[0] + (q[2] - q[0]) * fx; val ty = q[1] + (q[3] - q[1]) * fx
        val bx = q[6] + (q[4] - q[6]) * fx; val by = q[7] + (q[5] - q[7]) * fx
        return Offset(tx + (bx - tx) * fy + dx, ty + (by - ty) * fy + dy)
    }

    /** Los cuatro paños, corridos ([dx], [dy]) y crecidos [crece] (en fracción: la penumbra). */
    fun forma(dx: Float, dy: Float, crece: Float = 0f): Path {
        val p = Path()
        val barra = 0.035f
        val cortes = floatArrayOf(0f, 0.5f - barra, 0.5f + barra, 1f)
        val cortesY = floatArrayOf(0f, 0.4f - barra, 0.4f + barra, 1f)
        for (c in 0 until 2) for (f in 0 until 2) {
            val fx0 = cortes[c * 2] - crece; val fx1 = cortes[c * 2 + 1] + crece
            val fy0 = cortesY[f * 2] - crece; val fy1 = cortesY[f * 2 + 1] + crece
            val a = punto(fx0, fy0, dx, dy); val b = punto(fx1, fy0, dx, dy); val d = punto(fx1, fy1, dx, dy); val e = punto(fx0, fy1, dx, dy)
            p.moveTo(a.x, a.y); p.lineTo(b.x, b.y); p.lineTo(d.x, d.y); p.lineTo(e.x, e.y); p.close()
        }
        return p
    }

    /** Lo corrido de la luz en el tablero (más adelante que la pared) y en la tapa del registro (más adelante todavía). */
    val tablero = Offset(-16f * U, -7f * U)
    fun tapa(g: Float) = Offset(-20f * U - g * 0.5f, -9f * U - g * 0.2f)

    /** Cuánta luz da: de día, el sol de noviembre; de noche, la luna, menos. */
    fun fuerza(a: AireRevolucion) = if (a.oscuro) 0.5f else 0.85f
}

/**
 * La libreta de momentos de Revolución: el registro del regente en su pupitre, en la imprenta del barrio (la misma casa que el
 * Ideario y la biblioteca). El regente apunta ahí el trabajo de cada día: un registro grueso, de pie en el tablero inclinado del
 * pupitre, con las tapas forradas de papel verde cardenillo, el lomo redondo y las puntas de becerro, los nervios del lomo, los
 * cantos de las hojas salpicados de ocre rojo y una etiqueta de papel impresa con el nombre y la cuenta. Engorda con las
 * páginas (el grueso que asoma a la derecha y abajo, y los papeles que le salen por la cabeza). Arriba, en la repisa del
 * pupitre, el cuerno de tinta con su pluma, la salvadera y la vela de sebo en su palmatoria; detrás, el yeso ocre rosado con el
 * zócalo sangre de toro, la viga que cruza arriba y, a la derecha, el pie derecho de la prensa con la prueba colgada y el
 * ovillo de bramante. Debajo, los registros viejos atados; al pie, las losas de liais. Sin emblemas: lo que dice que es de la
 * Revolución es el mundo de la imprenta.
 *
 * La luz: de día, la ventana de la izquierda (sus cuatro paños en la pared, el tablero y el registro, corridos en cada plano;
 * el lado claro de cada cosa a la izquierda y las sombras a la derecha). De noche, la vela de la repisa: su charco enciende la
 * prensa, la derecha del registro y la repisa, y lo lejano (la izquierda, el suelo) se hunde en el añil y en el sangre de toro
 * hondo de la casa; por la ventana entra la luna, fría, en la pared y en los cantos de la izquierda. Lo fijo se pinta una vez
 * por tamaño y modo; el registro, en cada dibujo.
 */
internal object MuebleLibretaRevolucion {
    val ALTO: Dp = 262.dp

    /** Cuánta luz de la vela le llega a [p] (1 junto a la llama, poca lejos); de día, toda. */
    fun cerca(p: Offset, s: SitioLibretaRevolucion, a: AireRevolucion): Float {
        if (!a.oscuro) return 1f
        val dx = (p.x - s.llama.x) / s.U; val dy = (p.y - s.llama.y) / s.U
        return caida(sqrt(dx * dx + dy * dy))
    }

    /** La caída de la luz de la vela con la distancia [d] (en dp). */
    private fun caida(d: Float) = (1.45f * exp(-0.0115f * d)).coerceIn(0.03f, 1f)

    private val RADIOS = floatArrayOf(0f, 16f, 36f, 64f, 100f, 146f, 204f, 280f, 380f)

    /**
     * El color [col] (con [f] como en [AireRevolucion.dia]) puesto de una vez en todo el rincón: de noche, el charco de la vela
     * (un degradado redondo alrededor de la llama, sin escalones); de día, la ventana de la izquierda (más claro a la izquierda).
     * Con [pano], el mismo material bajo la luz de la ventana.
     */
    fun pincel(col: Color, f: Float, s: SitioLibretaRevolucion, a: AireRevolucion, pano: Float = 0f, local: Float = 0f): Brush {
        fun c(base: Color) = if (pano > 0f) a.sol(col, base, pano) else base
        return if (a.oscuro) {
            val r = RADIOS.last()
            Brush.radialGradient(*Array(RADIOS.size) { i -> RADIOS[i] / r to c(tinte(col, f, caida(RADIOS[i]), a, local)) }, center = s.llama, radius = r * s.U)
        } else Brush.horizontalGradient(listOf(c(a.dia(col, f + 0.07f)), c(a.dia(col, f - 0.09f))), 0f, s.w)
    }

    /** Los pinceles del registro ya armados (cada uno, nueve colores calculados), por tamaño y modo: el registro se pinta en vivo. */
    private val pinceles = HashMap<String, Brush>()
    private var clavePinceles = ""

    /** Como [pincel], guardado: para lo que se pinta en cada dibujo. */
    fun pincelGuardado(col: Color, f: Float, s: SitioLibretaRevolucion, a: AireRevolucion, pano: Float = 0f, local: Float = 0f): Brush {
        val k = "${s.w}:${s.h}:${s.U}:${a.oscuro}:${a.cuerpo}"
        if (k != clavePinceles) { pinceles.clear(); clavePinceles = k }
        return pinceles.getOrPut("${col.value}:$f:$pano:$local") { pincel(col, f, s, a, pano, local) }
    }

    /**
     * El color de [col] de noche con [k] de luz de la vela, conservando [local] de su color propio (el papel verde de las tapas
     * sigue verde bajo la vela: la luz lo tiñe, no lo vuelve pardo). De día, como [AireRevolucion.dia].
     */
    fun tinte(col: Color, f: Float, k: Float, a: AireRevolucion, local: Float = 0f): Color {
        val base = a.dia(col, f, k)
        if (!a.oscuro || local <= 0f) return base
        val m = 0.2f + 0.5f * k
        return mezcla(base, Color(col.red * m, col.green * m, col.blue * m, col.alpha), local)
    }

    private var clave = ""
    private var imagen: ImageBitmap? = null

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val s = SitioLibretaRevolucion(w, h, density, e.hojas)
        val a = AireRevolucion(oscuro, false)
        val ac = AireRevolucion(oscuro, false, cuerpo = true)
        clipRect(0f, 0f, w, h) {
            // La pared, la viga, la prensa, el pupitre con su repisa y lo de debajo: una imagen por tamaño y modo.
            val k = "${w.toInt()}:${h.toInt()}:$oscuro:$density"
            var img = imagen
            if (img == null || clave != k) {
                val nueva = ImageBitmap(ceil(w).toInt().coerceAtLeast(1), ceil(h).toInt().coerceAtLeast(1))
                CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(nueva.width.toFloat(), nueva.height.toFloat())) {
                    with(RinconLibretaRevolucion) { rincon(s, a, ac) }
                }
                imagen = nueva; clave = k; img = nueva
            }
            drawImage(img)
            // El registro, en vivo (engorda con las páginas y se abre); y la llama encima de todo.
            with(RegistroLibretaRevolucion) { registro(e, s, ac, medidor) }
            with(RinconLibretaRevolucion) { llama(s, a) }
            granoMueble(Rect(0f, 0f, w, h), oscuro, 0.7f)
        }
    }
}
