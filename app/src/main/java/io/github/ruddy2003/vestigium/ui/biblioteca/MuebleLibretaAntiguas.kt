package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
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

/**
 * La libreta de momentos de Tierras Antiguas (la usa `EstanteAntiguas`, en `BibliotecaFantasia.kt`): el rincón del hogar en la
 * misma sala de arriba de la posada del paso donde está el armario de la crónica. Contra el enlucido de lapislázuli con su
 * sembrado de oro viejo y el friso de tablas de roble, entre el pie derecho de la izquierda con su zapata y la viga, y el hogar
 * de piedra de la derecha con su campana encalada, su repisa con dos platos vidriados, el llar con el caldero y el fuego sobre
 * los morillos (el fuelle cuelga de la jamba), está la mesa del cartógrafo de la compañía: de roble, con el faldón de rombos
 * tallados y un cajón, sobre patas torneadas. En ella, de pie en su atril, el libro de mapas: grande, de cordobán rojo con el
 * marco dorado, las cantoneras de latón, la rosa de los vientos y los montes gofrados, la cartela con el nombre y la cuenta y los
 * dos broches; engorda con los mapas, que asoman doblados por el canto y por la cabeza. Bajo él, un mapa desplegado cuelga por el
 * borde de la mesa. Al lado, el reloj de arena y los cuencos de colores con el pincel y el compás de puntas; detrás, el jalón
 * pintado de rojo y crudo, apoyado en la pared.
 *
 * De día el sol entra por una ventana de la pared de la izquierda (fuera del cuadro): su paño, con la forma de los dos arcos de
 * la ventana y el parteluz, cae en la pared y deja la sombra del libro y del jalón. De noche manda el fuego del hogar: su charco
 * cálido enciende la jamba, el caldero, la parte derecha de la mesa y del libro, y lejos todo se hunde en un añil casi negro (la
 * pared) y en un pardo rojizo (la madera); por la ventana entra la luna, fría, con el mismo paño. Lo fijo (todo menos el libro)
 * se pinta una vez por tamaño y modo en una imagen guardada ([CapaLibretaAntiguas]); el libro, en cada dibujo.
 */
internal object MuebleLibretaAntiguas {
    val ALTO: Dp = 262.dp

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w < 8f || h < 8f) return
        val s = SitioLibretaAntiguas(w, h, density)
        val L = LuzLibretaAntiguas(s, oscuro)
        clipRect {
            with(CapaLibretaAntiguas) { pegar("$w:$h:$oscuro:$density") { rincon(s, L) } }
            with(LibroLibretaAntiguas) { libro(e, medidor, s, L) }
        }
    }

    /** Todo lo que no cambia con las hojas ni al abrir: la pared, el pie derecho, el hogar, el suelo, la mesa y lo que hay en ella. */
    private fun DrawScope.rincon(s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        with(RinconLibretaAntiguas) {
            pared(s, L)
            madera(s, L)
            with(HogarLibretaAntiguas) { hogar(s, L) }
            pano(s, L)
            jalon(s, L)
            mesa(s, L)
            cosas(s, L)
        }
        with(HogarLibretaAntiguas) { fuego(s, L) }
        granoMueble(Rect(0f, 0f, s.w, s.h), L.oscuro)
    }
}

/** Un rombo de radios [rx] (a lo ancho) y [ry] (a lo alto) alrededor de [c]. */
internal fun romboLibretaAntiguas(c: Offset, rx: Float, ry: Float) = poligonoAntiguas(c.x, c.y - ry, c.x + rx, c.y, c.x, c.y + ry, c.x - rx, c.y)

/**
 * Dónde va cada cosa del rincón, en píxeles, según el ancho [w] y el alto [h] del lienzo: el entorno se estira a lo ancho y el
 * libro no se deforma (en un teléfono angosto se achica un poco y se quedan fuera los cuencos y el reloj de arena).
 */
internal class SitioLibretaAntiguas(val w: Float, val h: Float, val u: Float) {
    /** El suelo, la viga del techo y el arranque del friso de tablas. */
    val suelo = h * 0.905f
    val viga = h * 0.05f
    val friso = h * 0.575f
    /** El pie derecho de la izquierda (su borde derecho) y la zapata. */
    val poste = h * 0.068f
    val zapata = h * 0.2f
    /** El hogar: la cara izquierda de la jamba, su ancho, la repisa, el dintel y la boca. */
    val xJamba = maxOf(w - h * 0.5f, h * 0.68f)
    val jamba = h * 0.1f
    val repisa = h * 0.33f
    val dintel0 = h * 0.352f
    val dintel1 = h * 0.44f
    val boca = Rect(xJamba + jamba, dintel1, w + 1f, suelo - h * 0.03f)
    /** El corazón del fuego. */
    val fuego = Offset(minOf(xJamba + jamba + h * 0.2f, w - h * 0.11f), h * 0.79f)
    /** El centro del paño de luz de la ventana de la izquierda (sol de día, luna de noche). */
    val pano = Offset(h * 0.265f, h * 0.3f)
    /** La mesa: el canto de atrás del tablero, el de delante, el bocel y el pie del faldón. */
    val mesaAtras = h * 0.6f
    val mesaFrente = h * 0.645f
    val canto = h * 0.667f
    val faldon = h * 0.73f
    /** El libro: el ancho de la tapa y dónde va (de pie en su atril, sobre la mesa). */
    private val libre0 = poste + h * 0.06f
    val anchoLibro = ((xJamba - libre0) * 0.72f).coerceIn(h * 0.32f, h * 0.44f)
    private val cx = (libre0 + xJamba) / 2 - h * 0.035f
    val libro = Rect(cx - anchoLibro / 2, h * 0.085f, cx + anchoLibro / 2, mesaAtras)
    /** El atril: el listón donde apoya el libro. */
    val liston = Rect(libro.left - h * 0.014f, libro.bottom - h * 0.012f, libro.right + h * 0.02f, libro.bottom + h * 0.016f)
    /** La mesa, de lado a lado. */
    val mesa0 = maxOf(poste + h * 0.035f, libro.left - h * 0.17f)
    val mesa1 = minOf(xJamba - h * 0.012f, libro.right + h * 0.22f)
    /** El mapa que cuelga por el borde de la mesa. */
    val mapa = Rect(libro.left + h * 0.05f, mesaFrente, libro.left + h * 0.05f + minOf(h * 0.32f, anchoLibro * 0.78f), h * 0.872f)
    /** El reloj de arena (a la izquierda del libro) y los cuencos de colores (a la derecha), si caben. */
    val reloj: Offset? = Offset(libro.left - h * 0.068f, mesaFrente - h * 0.012f).takeIf { it.x - h * 0.04f > poste + h * 0.085f }
    val cuencos: Offset? = Offset(libro.right + h * 0.115f, mesaFrente - h * 0.01f).takeIf { it.x + h * 0.08f < mesa1 }
}

/**
 * La luz del rincón. De día, tal cual (el sol se pinta aparte, con su paño). De noche (el modo oscuro), lo que llega del fuego
 * ([k], de 0 lejos a 1 junto a él) y lo que llega de la luna por la ventana ([l]): cada cosa se pinta con [AireAntiguas] (lejos,
 * hundida en un oscuro con color; cerca, a la luz de la llama) y la luna le pone encima su versión fría.
 */
internal class LuzLibretaAntiguas(val s: SitioLibretaAntiguas, val oscuro: Boolean) {
    /** El aire de la pared (lo que se hunde, en añil) y el de la madera y lo demás (lo que se hunde, en pardo rojizo). */
    val a = AireAntiguas(oscuro, false)
    val am = AireAntiguas(oscuro, false, true)

    /** Cuánta luz del fuego llega a [p]. */
    fun k(p: Offset): Float = if (!oscuro) 0f else (2f * exp(-2.4f * (p - s.fuego).getDistance() / s.h)).coerceIn(0f, 1f)

    /** Cuánta luna llega a [p] (lo que cae en el paño de la ventana y su alrededor). */
    fun l(p: Offset): Float = if (!oscuro) 0f else (0.55f * exp(-2.8f * (p - s.pano).getDistance() / s.h)).coerceIn(0f, 1f)

    /** El color de la pared en [p]. */
    fun pared(c: Color, p: Offset): Color = conLuna(a.luz(c, k(p)), c, l(p))

    /** El color de una cosa (madera, metal, cuero, barro…) en [p]. */
    fun cosa(c: Color, p: Offset): Color = conLuna(am.luz(c, k(p)), c, l(p))

    /** Con la luna: lo que toca se ve con su valor pero frío (la luna no da color). */
    private fun conLuna(noche: Color, dia: Color, l: Float): Color {
        if (l <= 0.01f) return noche
        val y = 0.3f * dia.red + 0.59f * dia.green + 0.11f * dia.blue
        val frio = Color(0.2f * y + 0.07f * dia.red, 0.27f * y + 0.07f * dia.green, 0.44f * y + 0.13f * dia.blue, dia.alpha)
        return mezcla(noche, frio, l)
    }

    /**
     * El charco del fuego sobre lo que ya se pintó con su color de día dentro de [r]: cerca, un tinte cálido leve; lejos, el
     * oscuro con color ([hondo]) casi entero. Se usa para la pared y la madera grandes, pintadas una vez.
     */
    fun DrawScope.charco(r: Rect, hondo: Color, calido: Color) {
        if (!oscuro) return
        val f = s.fuego
        drawRect(Brush.radialGradient(
            0f to calido.copy(alpha = 0.12f), 0.2f to mezcla(calido, hondo, 0.45f).copy(alpha = 0.45f),
            0.45f to hondo.copy(alpha = 0.85f), 1f to hondo.copy(alpha = 0.97f),
            center = f, radius = s.h * 1.3f), r.topLeft, r.size)
    }
}

/**
 * Lo fijo de la libreta (todo menos el libro), pintado una vez por tamaño y modo en una imagen guardada que luego se pega. Se
 * guarda solo la última, unos 3 MB con 1080 px de ancho.
 */
internal object CapaLibretaAntiguas {
    private var clave: String? = null
    private var imagen: ImageBitmap? = null

    fun DrawScope.pegar(clave: String, pintar: DrawScope.() -> Unit) {
        val ancho = ceil(size.width).toInt(); val alto = ceil(size.height).toInt()
        if (ancho < 1 || alto < 1) return
        var img = imagen
        if (img == null || this@CapaLibretaAntiguas.clave != clave) {
            val nueva = ImageBitmap(ancho, alto)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(ancho.toFloat(), alto.toFloat())) { pintar() }
            imagen = nueva; this@CapaLibretaAntiguas.clave = clave; img = nueva
        }
        drawImage(img, IntOffset.Zero, IntSize(img.width, img.height), IntOffset.Zero, IntSize(img.width, img.height))
    }
}
