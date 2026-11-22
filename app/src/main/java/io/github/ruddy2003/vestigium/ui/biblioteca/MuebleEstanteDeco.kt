package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

// =====================================================================
//  Art déco: el armario de partituras del club de Lou
// =====================================================================

/**
 * La paleta del club. El ébano de Macasar del armario (casi negro, con sus vetas rojizas y de miel), el latón de los filetes y
 * los herrajes, el níquel del brazo del gramófono, el galuchat verde pálido de los costados, el marfil de los filetes y las
 * etiquetas, el papel pintado color champán con sus abanicos, el terciopelo azul petróleo del fondo de los casilleros, el
 * granate del gramófono, el terrazo del suelo y las luces: la llama del aplique, el alba rosa de la escalera y el frío de la
 * calle. Lo cálido (Macasar, latón, champán, granate) y lo frío (petróleo, galuchat, níquel) van juntos.
 */
internal object PaletaDeco {
    val MACASAR = Color(0xFF5A321D)
    val MACASAR_NEGRO = Color(0xFF1E100A)
    val MACASAR_MIEL = Color(0xFFC0803F)
    val LATON = Color(0xFFCDA34A)
    val LATON_LUZ = Color(0xFFF4DE98)
    val LATON_OSC = Color(0xFF7C5A1A)
    val NIQUEL = Color(0xFFC6CBCD)
    val NIQUEL_OSC = Color(0xFF5B6366)
    val GALUCHAT = Color(0xFFB0C7B1)
    val GALUCHAT_OSC = Color(0xFF6C8B77)
    val MARFIL = Color(0xFFF0E6CB)
    val PAPEL = Color(0xFFDDB674)
    val PAPEL_OSC = Color(0xFFA07A3C)
    val TERCIOPELO = Color(0xFF1D4762)
    val TERCIOPELO_OSC = Color(0xFF0E283A)
    val GRANATE = Color(0xFF7C1F2B)
    val ESTRAZA = Color(0xFFBC915C)
    val TERRAZO = Color(0xFFDCCFB6)
    val TINTA = Color(0xFF1A1410)
    val LLAMA = Color(0xFFFFC46C)
    val PANTALLA = Color(0xFFFFEDC6)
    val ALBA = Color(0xFFEFA6A2)
    val LUNA = Color(0xFFA6BCE0)
    val NOCHE = Color(0xFF130B0E)
    val NOCHE_SUENOS = Color(0xFF0B1024)
    /** El volumen sin ánimo: un papel gris piedra, que no se confunde con ningún ánimo. */
    val SIN_ANIMO = Color(0xFF9C978C)
}

/**
 * La luz del momento. De día entra por la escalera de la calle, fuera del cuadro arriba a la izquierda: es un sótano, sin
 * ventana; los colores van tal cual, con su plano de luz y su plano de sombra. De noche alumbra el aplique de abanico de la
 * pared, a la derecha: cada cosa va de su color hondo (oscuro, pero de su mismo tono) a su color según lo que le llega ([c]),
 * con un poco de la llama. En los Sueños en claro, el alba rosa baja por la escalera y el aplique arde bajo; en oscuro, el
 * aplique está apagado y solo baja el frío de la calle.
 */
internal class AireDeco(val oscuro: Boolean, val noche: Boolean) {
    private companion object {
        val AIRE_ALBA = Color(0xFF9F86B0)
        fun multiplica(c: Color, m: Color) = Color(c.red * m.red, c.green * m.green, c.blue * m.blue, c.alpha)
    }

    /** Si alumbra una luz dibujada (el aplique o la calle). */
    val lumbre: Boolean get() = oscuro || noche
    /** Los Sueños en oscuro: el aplique apagado, solo el frío de la escalera. */
    val luna: Boolean get() = oscuro && noche
    /** Los Sueños en claro: el alba por la escalera. */
    val alba: Boolean get() = !oscuro && noche
    /** Si el aplique está encendido. */
    val aplique: Boolean get() = lumbre && !luna
    /** El oscuro de la noche: un vino casi negro; en los Sueños, un azul de madrugada. */
    val nocheColor: Color get() = if (luna) PaletaDeco.NOCHE_SUENOS else PaletaDeco.NOCHE
    /** El color de la luz que manda. */
    val foco: Color get() = if (luna) PaletaDeco.LUNA else PaletaDeco.LLAMA

    /** El color propio de algo, hundido: oscuro, de su mismo tono, con el aire de la noche. */
    fun hondo(c: Color): Color {
        val n = nocheColor
        return Color(c.red * 0.17f + n.red * 0.8f, c.green * 0.17f + n.green * 0.8f, c.blue * 0.17f + n.blue * 0.8f, c.alpha)
    }

    /** Un color con la luz [k] (de 0 a 1) que le llega. */
    fun c(col: Color, k: Float): Color {
        if (!lumbre) return col
        val kk = k.coerceIn(0f, 1f)
        if (alba) return mezcla(multiplica(col, mezcla(Color.White, AIRE_ALBA, 0.85f * (1f - kk))), PaletaDeco.LLAMA, 0.1f * kk)
        val v = mezcla(hondo(col), col, (1.05f * kk).coerceAtMost(0.95f))
        return mezcla(v, foco, (if (luna) 0.06f else 0.14f) * kk)
    }
    /** La sombra: de día, violeta hondo; al alba, más lila; de noche, la noche. */
    fun s(a: Float): Color = when {
        !lumbre -> sombraMueble(a)
        alba -> mezcla(SOMBRA_MUEBLE, AIRE_ALBA, 0.3f).copy(alpha = a.coerceIn(0f, 1f))
        else -> nocheColor.copy(alpha = (a * 1.3f).coerceIn(0f, 1f))
    }
    /** El brillo de lo que mira a la luz: de día, crema; de noche, el reflejo de la llama, según lo que le llega. */
    fun b(a: Float, k: Float = 1f): Color = when {
        !lumbre -> luzMueble(a)
        alba -> luzMueble(a * 0.6f)
        else -> foco.copy(alpha = (a * 0.95f * k.coerceIn(0f, 1f)).coerceIn(0f, 1f))
    }
    /** El color del ánimo: de noche se apaga menos que lo demás, para que se adivine aun en la repisa más lejana. */
    fun animo(t: Color, k: Float): Color {
        if (!lumbre) return t
        val kk = k.coerceIn(0f, 1f)
        if (alba) return mezcla(multiplica(t, mezcla(Color.White, AIRE_ALBA, 0.55f * (1f - kk))), PaletaDeco.LLAMA, 0.06f * kk)
        return mezcla(mezcla(hondo(t), t, (0.3f + 0.75f * kk).coerceAtMost(0.96f)), foco, (if (luna) 0.05f else 0.12f) * kk)
    }
    /** El marfil de las etiquetas, donde va el día: casi no se apaga, para que el número se lea siempre. */
    fun etiqueta(k: Float): Color {
        if (!lumbre) return PaletaDeco.MARFIL
        val kk = k.coerceIn(0f, 1f)
        if (alba) return mezcla(PaletaDeco.MARFIL, AIRE_ALBA, 0.18f * (1f - kk))
        return mezcla(mezcla(hondo(PaletaDeco.MARFIL), PaletaDeco.MARFIL, 0.58f + 0.38f * kk), foco, 0.12f * kk)
    }
    /** La tinta de lo escrito sobre marfil o latón. */
    val tinta: Color get() = PaletaDeco.TINTA
}

/**
 * Dónde está cada cosa del estante, en píxeles, y cuánta luz le llega. Lo arma [MuebleEstanteDeco.estante] una vez por
 * dibujo y lo comparten todas las piezas.
 */
internal class GeoDeco(d: DisposicionEstante, val w: Float, val h: Float, val u: Float, val a: AireDeco) {
    /** Lo bajo de la corona (donde empieza la primera repisa), el número de repisas y lo que mide una con su tabla. */
    val ch = d.corona.bottom
    val n = d.repisas.size
    val fila = if (n > 0 && d.tablas.isNotEmpty()) d.repisas[0].height + d.tablas[0].height else MuebleEstanteDeco.FILA * u
    /** El ancho de cada costado y lo que asoma la pared por fuera. */
    val lado = w * MuebleEstanteDeco.LADO
    val costado = w * MuebleEstanteDeco.COSTADO
    /** La cornisa escalonada: lo alto de cada grada, de arriba abajo, y dónde apoyan los objetos de encima. */
    val losa = w * 0.036f
    val grada = w * 0.02f
    val friso = w * 0.042f
    val cara = w * 0.022f
    val cornisaArriba = ch - friso - grada - losa
    val apoyo = cornisaArriba - cara * 0.45f
    /** El pie: lo alto de los cajones, de las patas y dónde empieza el suelo. */
    val pie = d.base.top
    val cajones = pie + d.base.height * 0.36f
    val suelo = pie + d.base.height * 0.56f

    /** De la altura en el lienzo a anchos: arriba, tal cual; en el cuerpo, por repisas, como lo cuentan los volúmenes. */
    fun fy(y: Float): Float = if (y <= ch) y / w else MuebleEstanteDeco.CORONA + (y - ch) / maxOf(1f, fila) * MuebleEstanteDeco.FILA / MuebleEstanteDeco.ANCHO_NOMINAL
    /** Y al revés. */
    fun py(f: Float): Float = if (f <= MuebleEstanteDeco.CORONA) f * w else ch + (f - MuebleEstanteDeco.CORONA) * MuebleEstanteDeco.ANCHO_NOMINAL / MuebleEstanteDeco.FILA * fila
    /** Cuánta luz le llega a un punto del lienzo (de día, 1). */
    fun k(p: Offset): Float = if (!a.lumbre) 1f else MuebleEstanteDeco.cerca(p.x / w, fy(p.y), a)
    /** Cuánto frío de la escalera le llega (solo de noche en el diario). */
    fun fr(p: Offset): Float = MuebleEstanteDeco.frio(a, p.x / w, fy(p.y))
    /** El color de algo en [p]. */
    fun c(col: Color, p: Offset): Color = a.c(col, k(p))
    /** Si la luz que manda en [p] viene de la izquierda (de día, al alba, con la calle) o del aplique, de la derecha. */
    fun izq(p: Offset): Boolean = MuebleEstanteDeco.luzIzquierda(a, p.x / w)
}

/**
 * El dibujo del estante del diario de Art déco (lo usa `EstanteDeco`, en `BibliotecaContemporanea.kt`): el armario de partituras
 * del club de sótano de Harlem donde toca Lou, el pianista de su «otra vida», hacia 1926. Es un armario de ebanista, de ébano de
 * Macasar chapeado en libro con su veta rojiza y de miel y su brillo de goma laca: cornisa escalonada con filetes de latón y un
 * friso de greca de marfil, costados forrados de galuchat con su filete de marfil y su capitel de latón, cada casillero con
 * su marco biselado, su filete de latón, el canto interior del costado y el fondo de terciopelo azul petróleo; abajo, dos cajones
 * con tiradores y bocallaves de latón, el zócalo escalonado y las patas con zapata de latón sobre un terrazo con abanico de
 * latón embutido. Detrás, el papel pintado color champán con abanicos y la moldura escalonada del techo. Sin ventana: es un
 * sótano; de día la luz baja por la escalera de la calle, fuera del cuadro arriba a la izquierda; de noche arde el aplique de
 * abanico de vidrio esmerilado y tiñe la pared. Encima, el bodegón: el gramófono portátil abierto (el protagonista), los discos
 * en sus fundas, el metrónomo, la taza (en la Ley Seca, lo de la taza no siempre era café), la hoja pautada que cuelga del canto
 * y la trompeta colgada de su clavija. En los huecos, discos de canto, la sordina, la caja de cañas, la lata de agujas, el
 * cenicero, el rollo de partituras y las escobillas. Al pie, la pila de discos y el estuche de la trompeta, cortado por el borde.
 * De noche, además del aplique, baja por la izquierda el frío de la escalera ([frio]) y roza cantos y metal. Las entradas son la
 * caja del rollo de pianola, el álbum de discos y la carpeta de partituras, con el color de día del ánimo ([tonoDeDiaDeco]).
 * Distinto del Ideario, que es el casillero de laca negra de la recepción de un hotel. El mueble está en
 * `MuebleEstanteDecoMueble.kt`; la pared y lo que cuelga, en `MuebleEstanteDecoPared.kt`; lo de encima y lo del suelo, en
 * `MuebleEstanteDecoPiezas.kt`; lo de los huecos, en `MuebleEstanteDecoHuecos.kt`; las entradas, en
 * `MuebleEstanteDecoVolumenes.kt`; las texturas, en `MuebleEstanteDecoTeselas.kt`.
 */
internal object MuebleEstanteDeco {
    internal val LETRA: FontFamily = LetraExtra.pasaje

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.64f
    const val BASE = 0.46f
    const val LADO = 0.088f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 16.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (26 + (palabras / 34).coerceAtMost(20)).dp

    /** Cuál de los tres es una entrada: 0, la caja del rollo de pianola; 1, el álbum de discos; 2, la carpeta de partituras. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod(semilla xor (semilla ushr 5), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** La caja, la más alta; el álbum, el más bajo y macizo; la carpeta, en medio, con las hojas que asoman. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.88f + 0.05f * azar(semilla, 1)
        1 -> 0.8f + 0.04f * azar(semilla, 1)
        else -> 0.84f + 0.05f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    internal const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    internal const val FILA = 128f
    /** Lo que asoma la pared a cada lado del armario, en anchos. */
    internal const val COSTADO = 0.016f
    /** El aplique de abanico, en anchos (el medio del vidrio). */
    internal const val APL_X = 0.865f
    internal const val APL_Y = 0.2f
    /** Por dónde baja la luz de la escalera (fuera del cuadro, arriba a la izquierda), en anchos. */
    internal const val PUERTA_X = -0.08f
    internal const val PUERTA_Y = -0.06f
    /** De dónde baja el frío de la escalera de noche (la otra temperatura), en anchos: a la izquierda, abajo. */
    internal const val FRIO_X = -0.1f
    internal const val FRIO_Y = CORONA + 0.95f

    /** Cuánto alumbra el aplique un punto a [dx], [dy] de él, en anchos: cae con la distancia, más despacio hacia abajo. */
    fun alcanceAplique(dx: Float, dy: Float): Float {
        val yy = dy * (if (dy > 0f) 0.72f else 1f)
        return (1.35f * exp(-2.4f * sqrt(dx * dx + yy * yy))).coerceIn(0.03f, 1f)
    }

    /** Cuánto alumbra la escalera (el alba o el frío de la calle) un punto a [dx], [dy] de ella. */
    fun alcancePuerta(dx: Float, dy: Float): Float = (1.2f * exp(-2.0f * sqrt(dx * dx + dy * dy * 0.8f))).coerceIn(0.04f, 1f)

    /** La luz que llega a un punto en anchos: el frío de la calle en los Sueños en oscuro; al alba, la mayor; de noche, el aplique. */
    fun cerca(fx: Float, fy: Float, a: AireDeco): Float = when {
        a.luna -> alcancePuerta(fx - PUERTA_X, fy - PUERTA_Y)
        a.alba -> maxOf(0.75f * alcanceAplique(fx - APL_X, fy - APL_Y), alcancePuerta(fx - PUERTA_X, fy - PUERTA_Y))
        else -> alcanceAplique(fx - APL_X, fy - APL_Y)
    }

    /** Cuánto le llega el frío de la escalera a un punto, solo de noche en el diario (con el aplique encendido). */
    fun frio(a: AireDeco, fx: Float, fy: Float): Float {
        if (!a.oscuro || a.noche) return 0f
        val dx = fx - FRIO_X; val dy = fy - FRIO_Y
        return (1f - sqrt(dx * dx + dy * dy * 0.6f) / 1.05f).coerceIn(0f, 1f).pow(1.1f)
    }

    /** Si la luz que manda viene de la izquierda (el día, el alba, la calle) o del aplique (de la derecha, si queda a su izquierda). */
    fun luzIzquierda(a: AireDeco, fx: Float): Boolean = !(a.aplique && !a.alba) || fx > APL_X

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w <= 1f || h <= 1f) return
        val a = AireDeco(oscuro, noche)
        val g = GeoDeco(d, w, h, density, a)
        // La pared y lo que cuelga de ella.
        paredDeco(g, d)
        trompetaDeco(g)
        apliqueDeco(g)
        // El armario: los casilleros, los costados, las tablas con su rótulo, la cornisa y el pie.
        sombraArmarioDeco(g, d)
        d.repisas.forEachIndexed { k, r -> casilleroDeco(g, k, r, d.ocupado.getOrNull(k)) }
        costadosDeco(g, d)
        d.tablas.forEachIndexed { k, r -> tablaDeco(g, k, r) }
        d.repisas.forEachIndexed { k, r ->
            val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
            huecoDeco(g, k, r, oc)
        }
        d.placas.forEach { p -> rotuloDeco(g, p, medidor) }
        cornisaDeco(g)
        pieDeco(g, d)
        // Lo de encima, de atrás adelante.
        encimaDeco(g)
        // La luz del aplique encima de lo que tiene cerca, y la otra temperatura.
        luzFinalDeco(g)
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    // ---------------------------------------------------------------- cada entrada

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width <= 1f || size.height <= 1f) return
        val a = AireDeco(oscuro, v.noche)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val fy = CORONA + (v.repisa * FILA + ALTO_REPISA.value * 0.55f) / ANCHO_NOMINAL
        val k = if (a.lumbre) cerca(fx, fy, a) else 1f
        val izq = luzIzquierda(a, fx)
        val tono = cuerpoDeco(tonoDeDiaDeco(v.tono, oscuro), v.tono == null)
        val cuerpo = when (clase(v.semilla)) {
            0 -> rolloDeco(v, tono, medidor, a, k, izq)
            1 -> albumDeco(v, tono, medidor, a, k, izq)
            else -> carpetaDeco(v, tono, medidor, a, k, izq)
        }
        // De noche, el frío de la escalera roza el canto izquierdo de los que le quedan cerca (abajo a la izquierda).
        val kf = frio(a, fx, fy)
        if (kf > 0.02f) {
            drawRect(PaletaDeco.LUNA.copy(alpha = 0.6f * kf), cuerpo.topLeft, Size(1.3f * density, cuerpo.height))
            drawRect(PaletaDeco.LUNA.copy(alpha = 0.12f * kf), cuerpo.topLeft, cuerpo.size, blendMode = BlendMode.Screen)
        }
    }
}

/** Las formas comunes de la biblioteca de Art déco. */
internal object TrazoDeco {
    fun corrido(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

    fun poligono(vararg xy: Float) = Path().apply {
        moveTo(xy[0], xy[1])
        for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
        close()
    }

    /** Un rectángulo con las cuatro esquinas cortadas en escalón, como las placas y las etiquetas déco. */
    fun escalon(r: Rect, e: Float): Path = Path().apply {
        val m = e * 0.5f
        moveTo(r.left + e, r.top); lineTo(r.right - e, r.top); lineTo(r.right - e, r.top + m); lineTo(r.right - m, r.top + m)
        lineTo(r.right - m, r.top + e); lineTo(r.right, r.top + e); lineTo(r.right, r.bottom - e); lineTo(r.right - m, r.bottom - e)
        lineTo(r.right - m, r.bottom - m); lineTo(r.right - e, r.bottom - m); lineTo(r.right - e, r.bottom); lineTo(r.left + e, r.bottom)
        lineTo(r.left + e, r.bottom - m); lineTo(r.left + m, r.bottom - m); lineTo(r.left + m, r.bottom - e); lineTo(r.left, r.bottom - e)
        lineTo(r.left, r.top + e); lineTo(r.left + m, r.top + e); lineTo(r.left + m, r.top + m); lineTo(r.left + e, r.top + m); close()
    }
}

/**
 * Un filete de latón a lo ancho, de [x0] a [x1] en [y], de grueso [gr]: por tramos, para que de noche se encienda cerca del
 * aplique y se apague lejos, con su luz arriba y su sombra abajo.
 */
internal fun DrawScope.fileteDeco(g: GeoDeco, x0: Float, x1: Float, y: Float, gr: Float, tramos: Int = 6, color: Color = PaletaDeco.LATON) {
    if (x1 <= x0 || gr <= 0f) return
    val n = if (g.a.lumbre) tramos.coerceAtLeast(1) else 1
    val paso = (x1 - x0) / n
    for (i in 0 until n) {
        val p = Offset(x0 + paso * (i + 0.5f), y)
        val k = g.k(p)
        drawRect(g.a.c(color, k), Offset(x0 + paso * i, y), Size(paso + 0.5f, gr))
        drawRect(g.a.b(0.6f, k), Offset(x0 + paso * i, y), Size(paso + 0.5f, gr * 0.38f))
    }
    drawRect(g.a.s(0.3f), Offset(x0, y + gr), Size(x1 - x0, minOf(gr, 1.2f * g.u)))
}

/** Lo mismo, a lo alto, de [y0] a [y1] en [x]: la luz a la izquierda. */
internal fun DrawScope.fileteVDeco(g: GeoDeco, x: Float, y0: Float, y1: Float, gr: Float, tramos: Int = 6, color: Color = PaletaDeco.LATON) {
    if (y1 <= y0 || gr <= 0f) return
    val n = if (g.a.lumbre) tramos.coerceAtLeast(1) else 1
    val paso = (y1 - y0) / n
    for (i in 0 until n) {
        val k = g.k(Offset(x, y0 + paso * (i + 0.5f)))
        drawRect(g.a.c(color, k), Offset(x, y0 + paso * i), Size(gr, paso + 0.5f))
        drawRect(g.a.b(0.55f, k), Offset(x, y0 + paso * i), Size(gr * 0.38f, paso + 0.5f))
    }
}

/**
 * Un paño de Macasar en [r]: el fondo pardo rojizo, las vetas a rayas de verdad (bandas negras y de miel que ondulan, en
 * espejo de un paño al otro si va chapeado en libro), y el brillo largo de la goma laca. [vertical] dice cómo corre la veta.
 * [k] es la luz que le llega.
 */
internal fun DrawScope.macasarDeco(g: GeoDeco, r: Rect, semilla: Int, vertical: Boolean, k: Float, espejo: Boolean = false, brillo: Boolean = true) {
    if (r.width <= 0.5f || r.height <= 0.5f) return
    val a = g.a
    drawRect(a.c(PaletaDeco.MACASAR, k), r.topLeft, r.size)
    val negras = Path(); val miel = Path()
    val largo = if (vertical) r.height else r.width
    val ancho = if (vertical) r.width else r.height
    val bandas = (ancho / maxOf(2f * g.u, g.w * 0.012f)).toInt().coerceIn(2, 40)
    val paso = ancho / bandas
    for (i in 0 until bandas) {
        val f = if (espejo) bandas - 1 - i else i
        val z = azar(semilla + f, 3)
        val grueso = paso * (0.25f + 0.45f * z)
        val ond = paso * 0.6f * (azar(semilla + f, 4) - 0.5f)
        val o0 = paso * i + paso * 0.2f
        val camino = if (z > 0.7f) miel else negras
        fun pt(t: Float, o: Float): Offset = if (vertical) Offset(r.left + o, r.top + t) else Offset(r.left + t, r.top + o)
        val a0 = pt(0f, o0); val a1 = pt(largo * 0.35f, o0 + ond); val a2 = pt(largo * 0.7f, o0 - ond); val a3 = pt(largo, o0 + ond * 0.4f)
        val b0 = pt(0f, o0 + grueso); val b1 = pt(largo * 0.35f, o0 + grueso + ond); val b2 = pt(largo * 0.7f, o0 + grueso - ond); val b3 = pt(largo, o0 + grueso + ond * 0.4f)
        camino.moveTo(a0.x, a0.y); camino.cubicTo(a1.x, a1.y, a2.x, a2.y, a3.x, a3.y)
        camino.lineTo(b3.x, b3.y); camino.cubicTo(b2.x, b2.y, b1.x, b1.y, b0.x, b0.y); camino.close()
    }
    clipRect(r.left, r.top, r.right, r.bottom) {
        drawPath(negras, a.c(PaletaDeco.MACASAR_NEGRO, k).copy(alpha = 0.72f))
        drawPath(miel, a.c(PaletaDeco.MACASAR_MIEL, k).copy(alpha = 0.55f))
        if (brillo) {
            // El brillo de la goma laca: dos franjas largas, una ancha y tenue, otra fina.
            if (vertical) {
                val x1 = r.left + r.width * (0.12f + 0.2f * azar(semilla, 1))
                drawRect(a.b(0.1f, k), Offset(x1, r.top), Size(r.width * 0.18f, r.height))
                drawRect(a.b(0.14f, k), Offset(x1 + r.width * 0.05f, r.top), Size(r.width * 0.04f, r.height))
            } else {
                drawRect(a.b(0.1f, k), Offset(r.left, r.top + r.height * 0.12f), Size(r.width, r.height * 0.24f))
                drawRect(a.b(0.16f, k), Offset(r.left, r.top + r.height * 0.18f), Size(r.width, r.height * 0.07f))
            }
        }
    }
}

/** Un cuerpo de caras planas dentro de [r]: el plano de luz a un lado y arriba, el de sombra al otro. */
internal fun DrawScope.carasDeco(a: AireDeco, r: Rect, k: Float, izq: Boolean, fuerza: Float = 1f) {
    val u = density
    drawRect(a.b(0.16f * fuerza, k), Offset(if (izq) r.left else r.right - r.width * 0.22f, r.top), Size(r.width * 0.22f, r.height))
    drawRect(a.s(0.22f * fuerza), Offset(if (izq) r.right - r.width * 0.28f else r.left, r.top), Size(r.width * 0.28f, r.height))
    drawRect(a.b(0.5f * fuerza, k), r.topLeft, Size(r.width, 1f * u))
    drawRect(a.b(0.32f * fuerza, k), Offset(if (izq) r.left else r.right - 1f * u, r.top), Size(1f * u, r.height))
}

/** Un cuerpo redondo de pie dentro de [r] (un cilindro): el claro, el brillo y la sombra en bandas. */
internal fun DrawScope.cilindroDeco(a: AireDeco, r: Rect, k: Float, izq: Boolean, fuerza: Float = 1f) {
    fun x(f: Float) = if (izq) r.left + r.width * f else r.right - r.width * f
    fun banda(f0: Float, f1: Float, c: Color) { val xa = minOf(x(f0), x(f1)); drawRect(c, Offset(xa, r.top), Size(r.width * (f1 - f0), r.height)) }
    banda(0f, 0.3f, a.b(0.18f * fuerza, k))
    banda(0.14f, 0.23f, a.b(0.36f * fuerza, k))
    banda(0.58f, 1f, a.s(0.16f * fuerza))
    banda(0.8f, 1f, a.s(0.22f * fuerza))
}

/** La sombra de contacto de lo que pisa algo, con el color de sombra del momento. */
internal fun DrawScope.contactoDeco(a: AireDeco, centro: Offset, ancho: Float, alto: Float, alfa: Float = 0.32f) =
    drawOval(a.s(alfa), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

/** El destello del latón o del vidrio: de día, un punto crema; de noche, la llama, si le llega. */
internal fun DrawScope.destelloDeco(a: AireDeco, c: Offset, r: Float, k: Float) {
    if (a.lumbre && !a.alba) {
        if (k > 0.08f) {
            drawCircle(a.foco.copy(alpha = (0.35f * k).coerceAtMost(0.3f)), r * 2.2f, c)
            drawCircle(Color(0xFFFFF2D2).copy(alpha = (1.1f * k).coerceAtMost(0.95f)), r, c)
        }
    } else drawCircle(luzMueble(if (a.alba) 0.55f else 0.9f), r, c)
}
