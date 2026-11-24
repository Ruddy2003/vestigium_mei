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
    /** El pupitre: sus lados abajo (el tablero, en escorzo, se angosta [escorzo] por lado arriba) y sus alturas. */
    val der = prensaIzq - 14f * U
    val pw = minOf(254f * U, der - 10f * U)
    val izq = der - pw
    val escorzo = 20f * U
    val viga = 9f * U
    /** La repisa de arriba: su cara de encima (donde se apoyan las cosas) y su canto. */
    val galeria = 38f * U
    val galeriaAbajo = 44f * U
    /** El canto de abajo del tablero, el listón de apoyo (su cara de encima y su canto), el faldón y las patas. */
    val reborde = 199f * U
    val liston = 206f * U
    val faldon = 212f * U
    val faldonAbajo = 228f * U
    val suelo = 232f * U
    val pata = 258f * U
    val zocalo = 184f * U
    /** El registro: la tapa, de pie en el listón y apoyada en el tablero, y su grueso (de 11 a 27 dp), corrido abajo a la derecha. */
    val g = (11f + 16f * sqrt(hojas.coerceIn(0, 240) / 240f)) * U
    val gx = g * 0.62f
    val gy = g * 0.4f
    val bw = minOf(150f * U, pw - 76f * U)
    private val bl = izq + maxOf(22f * U, pw * 0.12f)
    val registro = Rect(bl, 22f * U, bl + bw, 203f * U - gy)
    /** La vela de sebo en su palmatoria, en el listón a la derecha del registro, y su llama (de aquí sale la luz de la noche). */
    val vela = Offset(minOf(bl + bw + 30f * U + 27f * 0.62f * U, der - 16f * U), 203f * U)
    val llama = Offset(vela.x, vela.y - 22f * U)
    /** La salvadera y el cuerno de tinta con su pluma, en la repisa, a la derecha del registro (que tapa el medio de la repisa). */
    val salvadera = Offset(minOf(bl + bw + 20f * U, der - escorzo - 26f * U), galeria)
    val cuerno = Offset(minOf(bl + bw + 42f * U, der - escorzo - 6f * U), galeria)
    /** La luz de la ventana de la izquierda (de día, el sol; de noche, la luna): en la pared, en el tablero y en el registro. */
    val pano = PanoLibretaRevolucion(this)
}

/**
 * La luz de la ventana de la izquierda, que entra por una ventana de dos hojas: dos paños separados por la sombra del parteluz.
 * Cae en la pared, y en lo que está delante de la pared (el tablero, el registro) cae corrida hacia arriba a la izquierda,
 * tanto más cuanto más adelante está: el borde se quiebra en cada plano.
 */
internal class PanoLibretaRevolucion(s: SitioLibretaRevolucion) {
    private val U = s.U
    /** Las esquinas en la pared: arriba a la izquierda, arriba a la derecha, abajo a la derecha y abajo a la izquierda. */
    private val x0 = s.izq - 52f * U
    private val q = floatArrayOf(x0, 52f * U, x0 + 118f * U, 84f * U, x0 + 118f * U, 178f * U, x0, 146f * U)

    private fun punto(fx: Float, fy: Float, dx: Float, dy: Float): Offset {
        val tx = q[0] + (q[2] - q[0]) * fx; val ty = q[1] + (q[3] - q[1]) * fx
        val bx = q[6] + (q[4] - q[6]) * fx; val by = q[7] + (q[5] - q[7]) * fx
        return Offset(tx + (bx - tx) * fy + dx, ty + (by - ty) * fy + dy)
    }

    /** Los dos paños, corridos ([dx], [dy]) y crecidos [crece] (en fracción: la penumbra). */
    fun forma(dx: Float, dy: Float, crece: Float = 0f): Path {
        val p = Path()
        val cortes = floatArrayOf(0f, 0.46f, 0.54f, 1f)
        for (c in 0 until 2) {
            val fx0 = cortes[c * 2] - crece; val fx1 = cortes[c * 2 + 1] + crece
            val a = punto(fx0, -crece, dx, dy); val b = punto(fx1, -crece, dx, dy); val d = punto(fx1, 1f + crece, dx, dy); val e = punto(fx0, 1f + crece, dx, dy)
            p.moveTo(a.x, a.y); p.lineTo(b.x, b.y); p.lineTo(d.x, d.y); p.lineTo(e.x, e.y); p.close()
        }
        return p
    }

    /** Lo corrido de la luz en el tablero (más adelante que la pared) y en la tapa del registro (más adelante todavía). */
    val tablero = Offset(-16f * U, -7f * U)
    fun tapa(g: Float) = Offset(-22f * U - g * 0.5f, -10f * U - g * 0.2f)

    /** Cuánta luz da: de día, el sol de noviembre (en la madera y el cuero, menos: no se vuelve naranja); de noche, la luna. */
    fun fuerza(a: AireRevolucion) = if (a.oscuro) 0.5f else 0.8f
    val madera = 0.5f
}

/**
 * La libreta de momentos de Revolución: el registro del regente en su pupitre, en la imprenta del barrio (la misma casa que el
 * Ideario y la biblioteca). El regente apunta ahí el trabajo de cada día: un registro grueso y alto, de pie en el listón de
 * apoyo del pupitre y recostado en el tablero inclinado, con las tapas forradas de papel verde cardenillo que vuelan sobre el
 * bloque, el lomo redondo y las puntas de becerro, los nervios del lomo, los cantos de las hojas salpicados de ocre rojo, la
 * cinta del registro y una etiqueta de papel impresa con el nombre y la cuenta. Engorda con las páginas (el grueso que asoma a
 * la derecha y abajo, y los papeles que le salen por la cabeza). El pupitre, en escorzo: el tablero más ancho abajo, sus
 * cantos, el listón, el faldón con su cajón y las patas torneadas; arriba, en la repisa, la salvadera y el cuerno de tinta con
 * su pluma; en el listón, a la derecha del registro, la vela de sebo en su palmatoria; en el tablero, la prueba clavada. Detrás,
 * el yeso ocre rosado con el zócalo sangre de toro, la viga que cruza arriba y, a la derecha, el pie derecho de la prensa con
 * el ovillo de bramante. Debajo, los registros viejos atados; al pie, las losas de liais. Sin emblemas: lo que dice que es de
 * la Revolución es el mundo de la imprenta.
 *
 * La luz: de día, la ventana de la izquierda (sus dos paños en la pared, el tablero y el lomo, corridos en cada plano; el lado
 * claro de cada cosa a la izquierda y las sombras a la derecha). De noche, la vela del listón: su charco enciende media tapa
 * en su verde, el corte de las hojas, la prueba, el tablero y la prensa, y lo lejano (el lomo, la izquierda, el suelo) se
 * hunde en el añil y la ciruela de la casa; por la ventana entra la luna, fría, en la pared y en el filo de los nervios. Lo
 * fijo se pinta una vez por tamaño y modo; el registro, en cada dibujo.
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
    private fun caida(d: Float) = (1.55f * exp(-0.0088f * d)).coerceIn(0.03f, 1f)

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
        // Cerca de la llama, su propio color, encendido y tibio (más rojo, menos azul); lejos, hundido.
        val m = 0.16f + 1.0f * k
        return mezcla(base, Color((col.red * m * 1.14f).coerceAtMost(1f), (col.green * m * 1.02f).coerceAtMost(1f), (col.blue * m * 0.78f).coerceAtMost(1f), col.alpha), local)
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
