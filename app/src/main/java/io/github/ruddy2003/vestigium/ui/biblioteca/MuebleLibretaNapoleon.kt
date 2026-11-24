package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
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
 * Dónde va cada cosa del rincón de la libreta de Napoleónico, en píxeles, para un lienzo de [w] por [h] y un legajo de [hojas]
 * despachos. Todo se mide en [U] (un dp, ajustado al alto: el alto de la libreta es fijo y el ancho cambia con el teléfono). El
 * atril va algo a la izquierda del medio, para que a la derecha quepa la palmatoria; a los lados, la cortina y el tintero se
 * cortan con el borde si no caben.
 */
internal class SitioLibretaNapoleon(val w: Float, val h: Float, u: Float, hojas: Int) {
    val U = u * (h / (MuebleLibretaNapoleon.ALTO.value * u)).coerceIn(0.5f, 2f)
    /** El medio del atril. */
    val cx = maxOf(w * 0.47f, 118f * U)

    // ---------------------------------------------------------------- la mesa

    /** Donde el tablero toca la pared (su borde de atrás), su canto de delante y donde empieza el faldón. */
    val mesa = 192f * U
    val canto = 213f * U
    val faldon = 217f * U

    // ---------------------------------------------------------------- el atril

    /** La tabla empinada del atril (arriba, su remate recto con su moldura). */
    val atrilIzq = cx - 100f * U
    val atrilDer = cx + 100f * U
    val atrilArriba = 46f * U
    /** El listón de abajo: su cara de arriba (donde apoya el legajo) y su borde de abajo; y donde pisa el atril en el tablero. */
    val liston = 182f * U
    val listonAbajo = 197f * U
    val pie = 206f * U
    /** El grueso de la tabla, que se ve por la derecha (se mira un poco desde ese lado). */
    val tabla = 6f * U

    // ---------------------------------------------------------------- el legajo

    /** Cuánto engorda: de 0 (un despacho) a 1 (240 o más); rápido al principio y cada vez más despacio. */
    val lleno = sqrt((hojas.coerceIn(1, 240) - 1) / 239f)
    /**
     * El grueso del legajo y adónde corre de la cubierta de delante a la de atrás: hacia abajo (el atril está empinado y se mira
     * un poco desde arriba: se ve el canto de abajo, el que pisa el listón) y a la derecha (se mira un poco desde ese lado).
     */
    val grueso = (15f + 22f * lleno) * U
    val atras = Offset(grueso * 0.5f, grueso * 0.55f)
    /** La carpeta de vitela, por delante: el legajo pisa el listón con la de atrás, así que cuanto más grueso, más arriba queda esta. */
    val tapa = Rect(cx - 106f * U, liston - atras.y - 122f * U, cx + 78f * U, liston - atras.y)
    /** Los pliegos, que sobresalen de la vitela por la derecha y por arriba (su cara de delante, antes de correr hacia atrás). */
    val pliegos = Rect(tapa.left + 3f * U, tapa.top - 5f * U, tapa.right + 12f * U, tapa.bottom - 1.5f * U)
    /** El doblez de la vitela junto al lomo, a la izquierda. */
    val lomo = 12f * U
    /** La cinta de balduque que ciñe el legajo a lo ancho (sale del lomo y da la vuelta por el canto) y su lazo. */
    val cintaY = tapa.top + 92f * U
    val lazo = Offset(tapa.left + 40f * U, cintaY)
    /** Las dos cintas de atar de la vitela, en el canto de la derecha (a qué alto). */
    val atadura1 = tapa.top + 30f * U
    val atadura2 = tapa.top + 64f * U
    /** La cartela rayada a tinta en la vitela, con el nombre y la cuenta en dos renglones (encima de la cinta, más chica que la cara). */
    val rotulo = Rect(tapa.center.x - 58f * U, tapa.top + 16f * U, tapa.center.x + 62f * U, tapa.top + 70f * U)

    // ---------------------------------------------------------------- lo de la mesa

    /** La palmatoria de lacrar (el medio de su platillo) y su llama. */
    val palmatoria = Offset(minOf(cx + 140f * U, w - 24f * U), 208f * U)
    val llama = Offset(palmatoria.x, 155f * U)
    /** El tintero (el medio de su bandeja). */
    val tintero = Offset(maxOf(cx - 142f * U, 30f * U), 208f * U)
    /** Hasta dónde llega la cortina desde el borde izquierdo. */
    val cortina = minOf(50f * U, atrilIzq - 4f * U)
}

/**
 * La libreta de momentos de Napoleónico: el legajo de despachos en el atril del ayudante de campo, en otro rincón del mismo cuarto
 * requisado que la biblioteca y el Ideario. Sobre la mesa de escribir de caoba con su cuero verde, el atril de sobremesa,
 * empinado y bajo, con su remate recto de moldura, su puntal y su cremallera de latón; en él, apoyado en el listón, el legajo: los
 * pliegos doblados en su carpeta de vitela flexible (alabeada y manchada, con dos esquinas dobladas, sus cintas de atar en el
 * canto y la cartela rayada a tinta con el nombre y la cuenta), ceñidos con la cinta de balduque carmesí con su lazo, cuyos cabos
 * cuelgan por delante del listón; bajo la cinta, el último despacho, una carta doblada en tres y cerrada con su lacre. Engorda con los despachos (más canto abajo y a la derecha, más pliegos que asoman con su lacre). A la
 * izquierda, la cortina de seda verde recogida, por donde entra la luz, y el tintero de bronce con su salvadera y la pluma; a la
 * derecha, el portapliegos del correo colgado de su clavo y, en la mesa, la palmatoria de lacrar con su vela y la barra de lacre.
 *
 * De día, el sol de la ventana deja la trama de sus cuarterones en la pared, quebrada en el atril y en la vitela; de noche arde la
 * vela de la palmatoria (su charco enciende el canto del legajo, la mesa y la pared de ese lado) y por la izquierda entra la luna,
 * fría, que deja su trama en la pared y un filo en los cantos que la miran; lo demás se hunde en el azul de la noche (la pared) y
 * en la caoba honda (la mesa). Lo fijo (la pared, la cortina, la mesa, el atril y lo de encima) se pinta una vez por tamaño y
 * modo; el legajo, en cada dibujo. La paleta y la luz son las de la biblioteca ([PaletaNapoleon], [AireNapoleon]).
 */
internal object MuebleLibretaNapoleon {
    val ALTO: Dp = 254.dp

    /** Cuánta luz de la vela le llega a [p] (1 junto a la llama, poca lejos); de día, toda. */
    fun cerca(p: Offset, s: SitioLibretaNapoleon, a: AireNapoleon): Float {
        if (!a.oscuro) return 1f
        val dx = (p.x - s.llama.x) / s.U; val dy = (p.y - s.llama.y) / s.U
        return (1.8f * exp(-sqrt(dx * dx + dy * dy * 1.15f) / 150f)).coerceIn(0.02f, 1f)
    }

    /** Cuánta luz de la vela le llega a la pared a [d] dp de la llama (de noche): cae más deprisa que en lo de la mesa. */
    fun cercaPared(d: Float): Float = (1.3f * exp(-d / 105f)).coerceIn(0.02f, 1f)

    /**
     * La mancha de la ventana en el plano de la pared, en píxeles: de día, el sol, alto y empinado, que baja hacia el atril; de
     * noche, la luna, más arriba y más tenue. Lo que está más adelante la recibe corrida a la izquierda (`forma(1f, corrido)`).
     */
    fun mancha(s: SitioLibretaNapoleon, a: AireNapoleon): ManchaNapoleon {
        val U = s.U; val x = s.cx
        return if (a.oscuro) ManchaNapoleon(floatArrayOf(x - 176f * U, 0f, x - 104f * U, 8f * U, x - 72f * U, 118f * U, x - 144f * U, 110f * U), PaletaNapoleon.LUNA, 0.62f)
        else ManchaNapoleon(floatArrayOf(x - 150f * U, 6f * U, x - 58f * U, 18f * U, x - 12f * U, 178f * U, x - 104f * U, 168f * U), PaletaNapoleon.SOL, 0.9f)
    }

    private var clave = ""
    private var imagen: ImageBitmap? = null

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val s = SitioLibretaNapoleon(w, h, density, e.hojas)
        val a = AireNapoleon(oscuro, false)
        val ac = AireNapoleon(oscuro, false, cuerpo = true)
        clipRect(0f, 0f, w, h) {
            // La pared, la cortina, la mesa, el atril, el tintero y la palmatoria: una imagen por tamaño y modo.
            val k = "${w.toInt()}:${h.toInt()}:$oscuro:$density"
            var img = imagen
            if (img == null || clave != k) {
                val nueva = ImageBitmap(ceil(w).toInt().coerceAtLeast(1), ceil(h).toInt().coerceAtLeast(1))
                CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(nueva.width.toFloat(), nueva.height.toFloat())) {
                    with(RinconLibretaNapoleon) { rincon(s, a, ac) }
                }
                imagen = nueva; clave = k; img = nueva
            }
            drawImage(img)
            // El legajo, en vivo.
            with(LegajoLibretaNapoleon) { legajo(e, s, a, ac, medidor) }
            granoMueble(Rect(0f, 0f, w, h), oscuro, 0.7f)
        }
    }
}
