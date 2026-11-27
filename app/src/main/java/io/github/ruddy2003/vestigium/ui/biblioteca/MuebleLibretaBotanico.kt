package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
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
 * Dónde va cada cosa del rincón de la libreta de Botánico, en píxeles, para un lienzo de [w] por [h] y un herbario de [hojas]
 * pliegos. Todo se mide en [U] (un dp, ajustado al alto: el alto de la libreta es fijo y el ancho cambia con el teléfono). El
 * caballete va algo a la izquierda del medio, para que a la derecha quepa el quinqué; la hiedra, a la izquierda, se corta con el
 * borde si no cabe.
 */
internal class SitioLibretaBotanico(val w: Float, val h: Float, u: Float, hojas: Int) {
    val U = u * (h / (MuebleLibretaBotanico.ALTO.value * u)).coerceIn(0.5f, 2f)
    /** El medio del herbario (y del caballete). */
    val cx = maxOf(w * 0.45f, 118f * U).coerceAtMost(maxOf(w - 168f * U, 112f * U))

    // ---------------------------------------------------------------- la pared y la mesa

    /** La moldura de nogal de colgar cuadros (la de la biblioteca): arriba y abajo. */
    val moldura = 9f * U
    val molduraAbajo = 14f * U
    /** Donde el tablero toca la pared (su borde de atrás), su canto de delante, el fin del canto y el cajón. */
    val mesa = 182f * U
    val frente = 214f * U
    val canto = 222f * U

    // ---------------------------------------------------------------- el caballete

    /** La repisa del caballete, donde pisa el herbario: su cara de arriba y su borde de abajo; y su largo. */
    val repisa = 172f * U
    val repisaAbajo = 181f * U
    val repisaIzq = cx - 112f * U
    val repisaDer = cx + 120f * U
    /** Donde pisan las patas del caballete en el tablero, y la punta de arriba del mástil. */
    val pie = 205f * U
    val punta = 4f * U

    // ---------------------------------------------------------------- el herbario

    /** Cuánto engorda: de 0 (un pliego) a 1 (240 o más); rápido al principio y cada vez más despacio. */
    val lleno = sqrt((hojas.coerceIn(1, 240) - 1) / 239f)
    /** El grueso de los pliegos y adónde corre de la tapa de delante a la de atrás: arriba y a la derecha (se mira un poco desde ahí). */
    val grueso = (12f + 18f * lleno) * U
    val atras = Offset(grueso * 0.7f, -grueso * 0.38f)
    /** La tapa de delante: pisa la repisa. */
    val tapa = Rect(cx - 100f * U, repisa - 1f * U - 146f * U, cx + 96f * U, repisa - 1f * U)
    /** El lomo de piel, a la izquierda (lo que envuelve la tapa). */
    val lomo = 19f * U
    /** La etiqueta pegada con el nombre y la cuenta. */
    val etiqueta = Rect(tapa.left + 34f * U, tapa.top + 14f * U, tapa.right - 24f * U, tapa.top + 66f * U)
    /** Cuánto se ladea la carpeta en el caballete (grados, sobre su esquina de abajo a la izquierda): no queda del todo de frente. */
    val ladeo = 1.2f
    /**
     * El pliego de herbario que está de pie delante de la carpeta, en la repisa, apoyado en ella: abajo a la izquierda y a la
     * derecha, y arriba a la izquierda y a la derecha (algo corrido a la derecha: se recuesta). Del orden de la mitad del alto de la
     * carpeta, y siempre debajo de la etiqueta.
     */
    val pliego = floatArrayOf(cx + 16f * U, repisa - 1f * U, cx + 66f * U, repisa - 1f * U, cx + 69f * U, repisa - 71f * U, cx + 20f * U, repisa - 71f * U)
    /** Las dos cintas de algodón del canto de la derecha (a qué alto) y la pinza del caballete arriba. */
    val cinta1 = tapa.top + 44f * U
    val cinta2 = tapa.bottom - 38f * U
    val pinza = Offset(cx + 6f * U, tapa.top)

    // ---------------------------------------------------------------- lo de la mesa

    /** El quinqué (el medio de su pie y su llama), el tintero y la ramita sobre el secante. */
    val lampara = Offset(minOf(cx + 148f * U, w - 30f * U), 207f * U)
    val llama = Offset(lampara.x, 137f * U)
    val tintero = Offset(minOf(cx + 114f * U, lampara.x - 30f * U), 211f * U)
    val ramita = Offset(cx - 56f * U, 209f * U)
    /** La maceta de la hiedra, a la izquierda. */
    val maceta = Offset(((tapa.left - 4f * U) / 2f).coerceAtLeast(20f * U), 207f * U)
}

/**
 * La luz del rincón de la libreta de Botánico. De día, la de la ventana de arriba a la izquierda (fuera del cuadro), que deja
 * su paño en la pared y en el tablero; de noche, el quinqué de la mesa (la llama bajo su pantalla verde: su charco cálido en
 * la pared, el tablero y el canto del herbario) y, por la izquierda, la luna, fría, con su paño más chico arriba. Los tonos son
 * los de la biblioteca ([TonoBotanico]).
 */
internal class LuzLibretaBotanico(val s: SitioLibretaBotanico, val oscuro: Boolean) {
    val aire = AireBotanico(oscuro, false)

    /** El paño de la ventana (de día) o de la luna (de noche) en el plano de la pared, en píxeles, en el orden de las agujas. */
    val pano: FloatArray = run {
        val U = s.U; val x = s.tapa.left
        if (oscuro) floatArrayOf(-8f * U, 22f * U, x - 6f * U, 30f * U, x + 26f * U, 132f * U, -8f * U, 124f * U)
        else floatArrayOf(-8f * U, 16f * U, x + 52f * U, 26f * U, x + 104f * U, s.mesa + 2f * U, -8f * U, s.mesa + 2f * U)
    }
    /** El mismo paño en el tablero: entra por el borde de atrás y se tiende hacia delante. */
    val panoMesa: FloatArray = run {
        val U = s.U; val x = s.tapa.left
        if (oscuro) floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        else floatArrayOf(-8f * U, s.mesa, x + 104f * U, s.mesa, x + 136f * U, s.frente, -8f * U, s.frente)
    }

    /** El paño en dp (para saber cuánto está dentro un punto). */
    private val panoU = FloatArray(8) { pano[it] / s.U }

    /** La lumbre del quinqué que le llega a [p] (1 junto a la llama); de día, nada. */
    fun k(p: Offset): Float {
        if (!oscuro) return 0f
        val dx = (p.x - s.llama.x) / s.U; val dy = (p.y - s.llama.y) / s.U
        val d = sqrt(dx * dx * 1.05f + dy * dy * (if (dy > 0f) 0.7f else 1.2f))
        return (1.9f * exp(-d / 118f)).coerceIn(0.02f, 1f)
    }

    /** La luz de la ventana que le llega a [p]: de día, más cerca de arriba a la izquierda; de noche, la de la luna en su paño. */
    fun sol(p: Offset, enPano: Boolean = false): Float {
        if (oscuro) return if (enPano) AireBotanico.dentro(panoU, p.x / s.U, p.y / s.U, 7f) else 0f
        val dx = p.x / (380f * s.U) - 0.02f; val dy = p.y / (380f * s.U) * 0.62f
        return exp(-1.35f * sqrt(dx * dx + dy * dy))
    }

    /** El tono de lo que está en [p]; [cuerpo]: la madera del mueble (se hunde más de noche). */
    fun t(p: Offset, cuerpo: Boolean = false, enPano: Boolean = false): TonoBotanico {
        val arriba = oscuro && p.y < s.llama.y - 14f * s.U && kotlin.math.abs(p.x - s.llama.x) < 60f * s.U
        return TonoBotanico(aire, k(p), sol(p, enPano), cuerpo, arriba)
    }

    /** Un degradado a lo ancho con el color que da [col] en cada punto (de noche, la lumbre cambia sin escalones; de día, liso). */
    fun aLoAncho(x0: Float, x1: Float, y: Float, cuerpo: Boolean = false, col: (TonoBotanico) -> Color): Brush {
        if (x1 - x0 < 2f) return SolidColor(col(t(Offset((x0 + x1) / 2, y), cuerpo)))
        val n = if (oscuro) ((x1 - x0) / (24f * s.U)).toInt().coerceIn(3, 14) else 3
        return Brush.horizontalGradient(List(n) { i -> col(t(Offset(x0 + (x1 - x0) * i / (n - 1), y), cuerpo)) }, x0, x1)
    }

    /** Lo mismo a lo alto. */
    fun aLoAlto(y0: Float, y1: Float, x: Float, cuerpo: Boolean = false, col: (TonoBotanico) -> Color): Brush {
        if (y1 - y0 < 2f) return SolidColor(col(t(Offset(x, (y0 + y1) / 2), cuerpo)))
        val n = if (oscuro) ((y1 - y0) / (24f * s.U)).toInt().coerceIn(3, 12) else 3
        return Brush.verticalGradient(List(n) { i -> col(t(Offset(x, y0 + (y1 - y0) * i / (n - 1)), cuerpo)) }, y0, y1)
    }

    /** El paño de la ventana corrido [dx], [dy] píxeles: el que cae en lo que está más adelante que la pared. */
    fun panoCorrido(dx: Float, dy: Float) = FloatArray(8) { i -> pano[i] + if (i % 2 == 0) dx else dy }

    /** El paño ([q], en píxeles) como camino, agrandado [crece] píxeles desde su centro (su penumbra). */
    fun camino(q: FloatArray, crece: Float = 0f): Path {
        val cx = (q[0] + q[2] + q[4] + q[6]) / 4; val cy = (q[1] + q[3] + q[5] + q[7]) / 4
        val p = Path()
        for (i in 0 until 4) {
            val dx = q[2 * i] - cx; val dy = q[2 * i + 1] - cy
            val l = sqrt(dx * dx + dy * dy).coerceAtLeast(1e-3f)
            val x = q[2 * i] + dx / l * crece; val y = q[2 * i + 1] + dy / l * crece
            if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
        }
        p.close()
        return p
    }
}

/**
 * La libreta de momentos de Botánico: el herbario de momentos en el caballete de sobremesa del naturalista, en otro rincón del
 * mismo gabinete que el Ideario y la biblioteca (la pared salvia con su moldura de nogal, el roble claro, el latón, la piel, el
 * quinqué de pantalla verde y, de noche, el verde petróleo). En la mesa de escribir de roble, con su cajón, el caballete de mesa
 * (las dos patas abiertas, el mástil con su pinza de tornillo y la repisa con su listón); en él, el herbario: una carpeta de
 * cartón forrada de tela verde, con el lomo y las puntas de piel, la etiqueta pegada con el nombre y la cuenta y dos pares de
 * cintas de algodón anudadas en el canto; entre las tapas, los pliegos con sus secantes, de los que asoman por arriba y por la
 * derecha las puntas de lo prensado (frondas, espigas, hojas). Engorda con los pliegos. A la izquierda, la hiedra en su maceta,
 * que trepa por su caña hasta la moldura y corre por ella; delante, la ramita recién cogida sobre su secante, esperando la
 * prensa; a la derecha, el tintero con su pluma y el quinqué.
 *
 * Lo fijo (la pared, la hiedra, la mesa, el caballete y lo de encima) se pinta una vez por tamaño y modo; el herbario, en cada
 * dibujo.
 */
internal object MuebleLibretaBotanico {
    val ALTO: Dp = 256.dp

    private var clave = ""
    private var imagen: ImageBitmap? = null

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val s = SitioLibretaBotanico(w, h, density, e.hojas)
        val l = LuzLibretaBotanico(s, oscuro)
        clipRect(0f, 0f, w, h) {
            // La pared, la hiedra, la mesa, el caballete, el quinqué, el tintero y la ramita: una imagen por tamaño y modo.
            val k = "${w.toInt()}:${h.toInt()}:$oscuro:$density"
            var img = imagen
            if (img == null || clave != k) {
                val nueva = ImageBitmap(ceil(w).toInt().coerceAtLeast(1), ceil(h).toInt().coerceAtLeast(1))
                CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(nueva.width.toFloat(), nueva.height.toFloat())) {
                    with(RinconLibretaBotanico) { rincon(s, l) }
                }
                imagen = nueva; clave = k; img = nueva
            }
            drawImage(img)
            // El herbario, en vivo; y encima la pinza del caballete, que lo sujeta.
            with(HerbarioLibretaBotanico) { herbario(e, s, l, medidor) }
            with(RinconLibretaBotanico) { pinza(s, l) }
            granoMueble(Rect(0f, 0f, w, h), oscuro, 0.6f)
        }
    }
}
