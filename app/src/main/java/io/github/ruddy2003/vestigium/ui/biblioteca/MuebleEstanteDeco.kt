package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.pow
import kotlin.math.sqrt

// =====================================================================
//  Art déco: el armario de partituras del club de Lou
// =====================================================================

/**
 * La paleta del club (de 12 a 18 colores con nombre): el ébano de Macasar del armario, con sus vetas negras y de miel; el
 * galuchat verde pálido de los costados; el marfil de los filetes, las placas y las etiquetas; el níquel de los herrajes; el
 * pan de plata de la pared con su pátina; el cuero granate del gramófono; el terrazo del suelo; y las luces: el aplique de
 * abanico de noche, el alba rosa de la escalera y el frío de la calle.
 */
internal object PaletaDeco {
    val MACASAR = Color(0xFF4B2F1F)
    val MACASAR_NEGRO = Color(0xFF1C110B)
    val MACASAR_MIEL = Color(0xFFA9763F)
    val GALUCHAT = Color(0xFFB4C5B0)
    val GALUCHAT_OSC = Color(0xFF6F8A78)
    val MARFIL = Color(0xFFEEE4C9)
    val NIQUEL = Color(0xFFB7BCBB)
    val NIQUEL_OSC = Color(0xFF575E61)
    val PLATA = Color(0xFFA3A69F)
    val PLATA_LUZ = Color(0xFFDCD7C3)
    val PATINA = Color(0xFF86704A)
    val PATINA_FRIA = Color(0xFF5E6B73)
    val GRANATE = Color(0xFF6E1F2A)
    val TERRAZO = Color(0xFFC7B9A2)
    val TINTA = Color(0xFF1D1712)
    val APLIQUE = Color(0xFFFFC37C)
    val ALBA = Color(0xFFEFA6A2)
    val LUNA = Color(0xFFA6BCE0)
    val NOCHE = Color(0xFF15121F)
    val NOCHE_SUENOS = Color(0xFF0E1530)
    /** El volumen sin ánimo: un papel gris piedra, que no se confunde con ningún ánimo. */
    val SIN_ANIMO = Color(0xFF9C978C)
}

/**
 * La luz del momento. De día entra por la escalera de la calle, fuera del cuadro arriba a la izquierda: es un sótano, sin
 * ventana. De noche alumbra el aplique de abanico de la pared, a la derecha. En los Sueños en claro, el alba rosa baja por la
 * escalera y el aplique arde bajo (la sesión de después, que acaba cuando sale el sol); en oscuro, el aplique está apagado y
 * solo baja el frío de la calle.
 */
internal class AireDeco(val oscuro: Boolean, val noche: Boolean) {
    private companion object {
        val AIRE_ALBA = Color(0xFF9A86B0)
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
    val hondo: Color get() = if (oscuro) (if (noche) PaletaDeco.NOCHE_SUENOS else PaletaDeco.NOCHE) else SOMBRA_MUEBLE
    val foco: Color get() = if (luna) PaletaDeco.LUNA else PaletaDeco.APLIQUE

    fun luz(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(multiplica(c, mezcla(Color.White, AIRE_ALBA, 0.9f * (1f - k))), PaletaDeco.APLIQUE, 0.1f * k) else c
        return mezcla(mezcla(c, hondo, 0.92f - 0.84f * k.pow(0.75f)), foco, (if (luna) 0.08f else 0.24f) * k)
    }
    fun sombra(a: Float): Color = when {
        oscuro -> hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f))
        noche -> mezcla(SOMBRA_MUEBLE, AIRE_ALBA, 0.35f).copy(alpha = a.coerceIn(0f, 1f))
        else -> sombraMueble(a)
    }
    fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.12f else if (noche) a * 0.5f else a)
    fun filo(a: Float): Color = foco.copy(alpha = if (oscuro) a.coerceIn(0f, 1f) else if (noche) (a * 0.5f).coerceIn(0f, 1f) else 0f)
    /** El color del ánimo: de noche se apaga menos que lo demás, para que se adivine aun en la repisa más lejana. */
    fun animo(t: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(multiplica(t, mezcla(Color.White, AIRE_ALBA, 0.55f * (1f - k))), PaletaDeco.APLIQUE, 0.06f * k) else t
        return mezcla(mezcla(t, hondo, 0.6f - 0.52f * k), foco, (if (luna) 0.06f else 0.2f) * k)
    }
    /** El marfil de las etiquetas, donde va el día: casi no se apaga, para que el número se lea siempre. */
    fun etiqueta(cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(PaletaDeco.MARFIL, AIRE_ALBA, 0.2f * (1f - k)) else PaletaDeco.MARFIL
        return mezcla(mezcla(PaletaDeco.MARFIL, hondo, 0.44f - 0.36f * k), foco, 0.16f * k)
    }
    val tinta: Color get() = if (oscuro) Color(0xFF120E0B) else PaletaDeco.TINTA
}

/**
 * El dibujo del estante del diario de Art déco (lo usa `EstanteDeco`, en `BibliotecaContemporanea.kt`): el armario de partituras
 * del club de sótano de Harlem donde toca Lou, el pianista de su «otra vida», hacia 1926. Es un armario alto de ébano de Macasar
 * chapeado en libro, con los costados forrados de galuchat verde pálido, filetes de marfil, herrajes de níquel y patas
 * ahusadas con sus zapatas de marfil, sobre un suelo de terrazo con su junta de latón. Detrás, la pared de pan de plata, en
 * cuadros con su pátina. Sin ventana: es un sótano; de día la luz baja por la escalera de la calle, fuera del cuadro arriba a
 * la izquierda; de noche alumbra el aplique de abanico de vidrio esmerilado de la pared. Encima, el gramófono portátil abierto
 * (el protagonista), los discos en sus fundas apoyados en la pared, el metrónomo y la taza con su plato (en la Ley Seca, lo
 * de la taza no siempre era café); colgando del canto, una hoja pautada. En los huecos, discos, la lata de agujas y el
 * cenicero, las escobillas y el rollo de partituras. Al pie, el estuche de la trompeta y una pila de discos. Las entradas son
 * la caja del rollo de pianola (alta y delgada), el álbum de discos de tela (bajo y ancho, con el disco que asoma) y la
 * carpeta de partituras (blanda, con su cordón). Distinto del Ideario, que es el casillero de laca negra de la recepción
 * de un hotel. Las piezas (los objetos, el pie y las entradas) están en `MuebleEstanteDecoPiezas.kt`.
 */
internal object MuebleEstanteDeco {
    internal val LETRA: FontFamily = LetraExtra.pasaje

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.5f
    const val BASE = 0.24f
    const val LADO = 0.075f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 15.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (24 + (palabras / 36).coerceAtMost(20)).dp

    /** Cuál de los tres es una entrada: 0, la caja del rollo de pianola; 1, el álbum de discos; 2, la carpeta de partituras. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod(semilla xor (semilla ushr 5), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** La caja, alta y derecha; el álbum, bajo y macizo; la carpeta, en medio y blanda (con las hojas que asoman). */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.88f + 0.06f * azar(semilla, 1)
        1 -> 0.66f + 0.05f * azar(semilla, 1)
        else -> 0.8f + 0.05f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    internal const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    internal const val FILA = 127f
    /** Lo que asoma la pared a cada lado del armario, en anchos. */
    internal const val COSTADO = 0.018f
    /** El aplique de abanico, en anchos (el medio del vidrio). */
    internal const val APL_X = 0.82f
    internal const val APL_Y = 0.2f
    /** Por dónde baja la luz de la escalera (fuera del cuadro, arriba a la izquierda), en anchos. */
    internal const val PUERTA_X = -0.1f
    internal const val PUERTA_Y = -0.05f

    /** Cuánto alumbra el aplique un punto a [dx], [dy] de él: en un estante corto se acaba antes; desde cinco repisas no crece. */
    fun alcanceAplique(dx: Float, dy: Float, repisas: Int): Float {
        val radio = 0.85f + 0.1f * repisas.coerceIn(2, 5)
        return (1f - sqrt(dx * dx * 1.5f + dy * dy) / radio).coerceIn(0f, 1f).pow(1.3f)
    }

    /** Cuánto alumbra la escalera (el alba o el frío de la calle): llega menos lejos. */
    fun alcancePuerta(dx: Float, dy: Float, repisas: Int): Float {
        val radio = 0.85f + 0.08f * repisas.coerceIn(2, 5)
        return (1f - sqrt(dx * dx * 0.8f + dy * dy) / radio).coerceIn(0f, 1f).pow(1.3f)
    }

    /** La luz que llega a un punto en anchos: el frío de la calle en los Sueños en oscuro; al alba, la mayor; de noche, el aplique. */
    fun cerca(fx: Float, fy: Float, repisas: Int, a: AireDeco): Float = when {
        a.luna -> alcancePuerta(fx - PUERTA_X, fy - PUERTA_Y, repisas)
        a.alba -> maxOf(0.75f * alcanceAplique(fx - APL_X, fy - APL_Y, repisas), alcancePuerta(fx - PUERTA_X, fy - PUERTA_Y, repisas))
        else -> alcanceAplique(fx - APL_X, fy - APL_Y, repisas)
    }

    /** De día, cuánto le llega de la escalera a un punto (de 0.55 a 1): más claro arriba a la izquierda. */
    fun dia(fx: Float, fy: Float): Float = (1f - 0.45f * ((fx * 0.8f + fy * 0.5f) / 1.3f).coerceIn(0f, 1f))

    /** Si la luz que manda viene de la izquierda (el día, el alba, la calle) o del aplique (de la derecha, si queda a la izquierda de él). */
    fun luzIzquierda(a: AireDeco, fx: Float): Boolean = !(a.aplique && !a.alba) || fx > APL_X

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w <= 1f || h <= 1f) return
        val a = AireDeco(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val fila = if (n > 0 && d.tablas.isNotEmpty()) d.repisas[0].height + d.tablas[0].height else FILA * u
        fun fy(y: Float): Float = if (y <= ch) y / w else CORONA + (y - ch) / maxOf(1f, fila) * FILA / ANCHO_NOMINAL
        fun cerca(p: Offset): Float = if (!a.lumbre) 1f else cerca(p.x / w, fy(p.y), n, a)
        val tapaAlto = w * 0.036f
        val apoyo = ch - tapaAlto
        val pie = d.base.top
        val suelo = pie + d.base.height * 0.72f
        val costado = w * COSTADO
        val lado = w * LADO

        // ---- la pared de pan de plata: arriba, a los lados y entre las patas
        paredDeco(a, Rect(0f, 0f, w, apoyo + 2f * u), ::fy, n)
        paredDeco(a, Rect(0f, pie, w, suelo), ::fy, n)
        drawRect(a.luz(PaletaDeco.PLATA, cerca(Offset(costado, ch))), Offset(0f, apoyo), Size(costado + 1f, pie - apoyo))
        drawRect(a.luz(PaletaDeco.PLATA, cerca(Offset(w, ch))), Offset(w - costado - 1f, apoyo), Size(costado + 1f, pie - apoyo))
        if (a.aplique) abanicoEnPared(a, Offset(w * APL_X, w * APL_Y))
        // La sombra del armario en la pared: de día a la derecha; de noche a la izquierda, lejos del aplique.
        if (!a.aplique || a.alba) drawRect(a.sombra(0.34f), Offset(w - costado, apoyo), Size(costado, pie - apoyo))
        else drawRect(a.sombra(0.45f), Offset(0f, apoyo), Size(costado, pie - apoyo))

        // ---- el fondo de cada casillero: el Macasar chapeado en libro, quieto y hondo, con la sombra de la tabla de arriba
        d.repisas.forEachIndexed { k, r -> casilleroDeco(k, r, d.ocupado.getOrNull(k), a) { p -> cerca(p) } }

        // ---- los costados de galuchat, las tablas con su rótulo y lo de los huecos
        costadosDeco(a, apoyo, pie, n, fila, ::fy)
        d.tablas.forEachIndexed { k, r -> tablaDeco(r, k, a) { p -> cerca(p) } }
        d.repisas.forEachIndexed { k, r ->
            val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
            huecoDeco(k, r, oc, a, cerca(Offset((oc + r.right) / 2, r.center.y)), luzIzquierda(a, ((oc + r.right) / 2) / w))
        }
        d.placas.forEach { p -> rotuloDeco(p, medidor, a, cerca(p.r.center)) }

        // ---- la tapa del armario, con su sombra en la primera repisa
        tapaDeco(Rect(0f, apoyo, w, ch), a) { p -> cerca(p) }

        // ---- lo de encima, de atrás adelante
        encimaDeco(apoyo, a) { p -> cerca(p) }

        // ---- al pie: el faldón, las patas, el suelo y lo que hay en él
        pieDeco(d, lado, suelo, a) { p -> cerca(p) }

        // ---- de noche, el charco del aplique sobre el armario; al alba, el rosa de la escalera
        if (a.aplique) {
            mancha(Offset(w * APL_X, ch + w * 0.06f), w * 0.5f, PaletaDeco.APLIQUE.copy(alpha = if (a.oscuro) 0.16f else 0.08f))
            apliqueDeco(Offset(w * APL_X, w * APL_Y), a, encendido = true)
        } else apliqueDeco(Offset(w * APL_X, w * APL_Y), a, encendido = false)
        if (!a.oscuro) mancha(Offset(w * 0.02f, 0f), w * 0.9f, (if (a.alba) PaletaDeco.ALBA else Color(0xFFFFE2B0)).copy(alpha = if (a.alba) 0.22f else 0.2f))
        if (a.luna) mancha(Offset(0f, 0f), w * 0.8f, PaletaDeco.LUNA.copy(alpha = 0.12f))
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    /**
     * La pared de pan de plata en [r]: cuadros de hoja batida, cada uno de otro tono (la pátina dorada o fría que deja el tiempo),
     * con sus juntas finas y algún golpe. De noche cada cuadro toma la luz que le llega; de día, la de la escalera.
     */
    private fun DrawScope.paredDeco(a: AireDeco, r: Rect, fy: (Float) -> Float, n: Int) {
        if (r.width <= 1f || r.height <= 1f) return
        val u = density; val w = size.width
        val lado = maxOf(4f * u, w * 0.066f)
        clipRect(r.left, r.top, r.right, r.bottom) {
            val filas = ((r.height / lado).toInt() + 2).coerceAtMost(60)
            val cols = ((r.width / lado).toInt() + 2).coerceAtMost(40)
            val sombraJunta = Path(); val luzJunta = Path()
            for (i in 0 until filas) {
                val y = r.top + i * lado - lado * 0.3f
                for (j in 0 until cols) {
                    val x = j * lado - lado * 0.2f
                    // La hoja batida: casi toda del mismo gris de plata, con algún cuadro más pasado (la pátina) y otro más vivo.
                    val z = azar(901 + i, j)
                    val base = when {
                        z < 0.14f -> mezcla(PaletaDeco.PLATA, PaletaDeco.PATINA, 0.3f + 0.25f * azar(902 + i, j))
                        z < 0.24f -> mezcla(PaletaDeco.PLATA, PaletaDeco.PATINA_FRIA, 0.25f)
                        else -> mezcla(PaletaDeco.PLATA, PaletaDeco.PLATA_LUZ, 0.12f + 0.22f * azar(903 + i, j))
                    }
                    val c = Offset(x + lado / 2, y + lado / 2)
                    val k = if (a.lumbre) cerca(c.x / w, fy(c.y), n, a) else 1f
                    val col = if (a.lumbre) a.luz(base, k) else mezcla(base, SOMBRA_MUEBLE, 0.5f * (1f - dia(c.x / w, fy(c.y))))
                    drawRect(col, Offset(x, y), Size(lado + 0.6f, lado + 0.6f))
                    // Cada hoja monta sobre la de al lado: un filo claro arriba y a la izquierda, una sombra fina abajo.
                    luzJunta.moveTo(x, y + 0.8f * u); luzJunta.lineTo(x + lado, y + 0.8f * u)
                    luzJunta.moveTo(x + 0.8f * u, y); luzJunta.lineTo(x + 0.8f * u, y + lado)
                    sombraJunta.moveTo(x, y); sombraJunta.lineTo(x + lado, y)
                    sombraJunta.moveTo(x, y); sombraJunta.lineTo(x, y + lado)
                    // Una arruga de la hoja, en diagonal, en uno de cada tres.
                    if (azar(904 + i, j) < 0.34f) {
                        val f = azar(905 + i, j)
                        sombraJunta.moveTo(x + lado * f, y + lado * 0.1f); sombraJunta.lineTo(x + lado * (f * 0.5f + 0.3f), y + lado * 0.6f)
                    }
                }
            }
            drawPath(sombraJunta, a.sombra(0.2f), style = Stroke(0.6f * u))
            drawPath(luzJunta, a.claro(0.45f).copy(alpha = if (a.oscuro) 0.06f else 0.4f), style = Stroke(0.6f * u))
            // La pátina: manchas que bajan de las juntas, cálidas y frías.
            for (j in 0 until 10) {
                val x = r.left + r.width * azar(911, j)
                val y = r.top + r.height * azar(912, j)
                val col = if (j % 3 == 0) PaletaDeco.PATINA_FRIA else PaletaDeco.PATINA
                drawOval(a.luz(col, 0.5f).copy(alpha = 0.12f + 0.08f * azar(913, j)), Offset(x, y), Size(lado * (0.25f + 0.4f * azar(914, j)), lado * (0.6f + 1.1f * azar(915, j))))
            }
            // El brillo del metal: una franja ancha en diagonal, donde la plata devuelve la luz de la escalera.
            if (!a.oscuro) {
                val brillo = Path().apply {
                    moveTo(r.left + w * 0.05f, r.top); lineTo(r.left + w * 0.3f, r.top)
                    lineTo(r.left + w * 0.3f - r.height * 0.7f, r.bottom); lineTo(r.left + w * 0.05f - r.height * 0.7f, r.bottom); close()
                }
                drawPath(brillo, Color.White.copy(alpha = if (a.noche) 0.12f else 0.26f), blendMode = BlendMode.Screen)
                // Y un brillo más angosto al lado, y la franja apagada entre los dos: el metal no es parejo.
                drawPath(TrazoDeco.corrido(brillo, w * 0.34f), Color.White.copy(alpha = if (a.noche) 0.06f else 0.14f), blendMode = BlendMode.Screen)
                drawPath(TrazoDeco.corrido(brillo, w * 0.2f), a.sombra(0.07f))
            }
        }
    }

    /** De noche, el abanico de luz que el aplique tira hacia arriba en la plata: tres capas de cuña, de ancha y tenue a fina. */
    private fun DrawScope.abanicoEnPared(a: AireDeco, c: Offset) {
        val w = size.width
        val fuerza = if (a.alba) 0.5f else 1f
        for ((abre, alfa) in listOf(0.5f to 0.1f, 0.34f to 0.14f, 0.2f to 0.18f)) {
            val alto = w * 0.42f
            val cuna = Path().apply {
                moveTo(c.x - w * 0.03f, c.y)
                lineTo(c.x - w * abre, maxOf(0f, c.y - alto))
                lineTo(c.x + w * abre, maxOf(0f, c.y - alto))
                lineTo(c.x + w * 0.03f, c.y)
                close()
            }
            drawPath(cuna, PaletaDeco.APLIQUE.copy(alpha = alfa * fuerza), blendMode = BlendMode.Screen)
        }
        mancha(c, w * 0.34f, PaletaDeco.APLIQUE.copy(alpha = 0.4f * fuerza))
    }

    /**
     * El fondo de un casillero: el Macasar en paños de chapa en libro (las vetas en espejo de un paño al otro), con su brillo de
     * goma laca en diagonal y la sombra de la tabla de arriba. De noche se pinta por tramos, cada uno con la luz que le llega.
     */
    private fun DrawScope.casilleroDeco(k: Int, r: Rect, oc: Float?, a: AireDeco, cerca: (Offset) -> Float) {
        val u = density; val w = size.width
        clipRect(r.left, r.top, r.right, r.bottom) {
            val fondo = mezcla(PaletaDeco.MACASAR, PaletaDeco.MACASAR_NEGRO, 0.35f)
            val tramos = if (a.lumbre) 10 else 1
            for (t in 0 until tramos) {
                val xa = r.left + r.width * t / tramos; val xb = r.left + r.width * (t + 1) / tramos + 0.6f
                drawRect(a.luz(fondo, cerca(Offset((xa + xb) / 2, r.center.y)) * 0.8f), Offset(xa, r.top), Size(xb - xa, r.height))
            }
            // Las vetas: bandas negras y de miel que ondulan, en espejo de un paño al siguiente.
            val panos = 4
            val negras = Path(); val miel = Path()
            for (p in 0 until panos) {
                val x0 = r.left + r.width * p / panos; val pw = r.width / panos
                for (j in 0 until 7) {
                    val f = 0.06f + 0.135f * j + 0.03f * azar(931, j)
                    val x = if (p % 2 == 0) x0 + pw * f else x0 + pw * (1f - f)
                    val camino = if (j % 3 == 1) miel else negras
                    val ond = pw * 0.05f * (if (p % 2 == 0) 1f else -1f)
                    camino.moveTo(x, r.top)
                    camino.cubicTo(x + ond, r.top + r.height * 0.33f, x - ond, r.top + r.height * 0.66f, x + ond * 0.5f, r.bottom)
                }
                negras.moveTo(x0, r.top); negras.lineTo(x0, r.bottom)
            }
            drawPath(negras, a.luz(PaletaDeco.MACASAR_NEGRO, 0.5f).copy(alpha = 0.55f), style = Stroke(2.2f * u))
            drawPath(miel, a.luz(PaletaDeco.MACASAR_MIEL, 0.6f).copy(alpha = if (a.oscuro) 0.25f else 0.35f), style = Stroke(1.3f * u))
            // El brillo de la goma laca: una franja ancha y tenue en diagonal (de noche, el reflejo del aplique cerca de él).
            if (!a.oscuro) {
                val x = r.left + r.width * (0.18f + 0.05f * k)
                val brillo = Path().apply {
                    moveTo(x, r.top); lineTo(x + r.width * 0.07f, r.top); lineTo(x + r.width * 0.02f, r.bottom); lineTo(x - r.width * 0.05f, r.bottom); close()
                }
                drawPath(brillo, Color.White.copy(alpha = if (a.noche) 0.04f else 0.07f), blendMode = BlendMode.Screen)
            } else if (a.aplique) {
                val kc = cerca(Offset(w * APL_X, r.center.y))
                if (kc > 0.1f) drawRect(PaletaDeco.APLIQUE.copy(alpha = 0.22f * kc), Offset(w * (APL_X - 0.05f), r.top), Size(w * 0.06f, r.height), blendMode = BlendMode.Screen)
            }
            // La sombra de la tabla de arriba, honda junto a ella; los costados, en sombra.
            val f = if (a.oscuro) 1.2f else 1f
            drawRect(a.sombra(0.34f * f), r.topLeft, Size(r.width, 6f * u))
            drawRect(a.sombra(0.18f * f), r.topLeft, Size(r.width, 16f * u))
            drawRect(a.sombra(0.08f * f), r.topLeft, Size(r.width, 34f * u))
            drawRect(a.sombra(0.22f), r.topLeft, Size(7f * u, r.height))
            drawRect(a.sombra(0.2f), Offset(r.right - 7f * u, r.top), Size(7f * u, r.height))
            // La sombra del último volumen, hacia donde no está la luz.
            if (oc != null && oc > r.left + 6f * u) {
                val izq = luzIzquierda(a, oc / w)
                val largo = (if (a.lumbre) 16f else 10f) * u
                val sx = if (izq) oc - 1f * u else oc - 1f * u - largo
                if (izq) drawRect(a.sombra(if (a.oscuro) 0.4f else 0.26f), Offset(sx, r.top + r.height * 0.16f), Size(largo, r.height * 0.84f))
            }
        }
    }

    /**
     * Los costados del armario: el marco de Macasar y, dentro, el galuchat (la piel de raya, verde pálido, con su grano de
     * perlas y la «estrella» de perlas gruesas en el medio de cada paño), con su filete de marfil. De noche, por tramos.
     */
    private fun DrawScope.costadosDeco(a: AireDeco, arriba: Float, abajo: Float, n: Int, fila: Float, fy: (Float) -> Float) {
        val u = density; val w = size.width
        val costado = w * COSTADO; val lado = w * LADO
        listOf(Rect(costado, arriba, lado, abajo), Rect(w - lado, arriba, w - costado, abajo)).forEachIndexed { i, p ->
            if (p.width <= 1f || p.height <= 1f) return@forEachIndexed
            val fx = p.center.x / w
            val marco = p.width * 0.2f
            val tramos = if (a.lumbre) (p.height / maxOf(1f, fila * 0.25f)).toInt().coerceIn(4, 80) else 1
            val piel = Rect(p.left + marco, p.top + marco * 1.6f, p.right - marco, p.bottom - marco)
            for (t in 0 until tramos) {
                val ya = p.top + p.height * t / tramos; val yb = p.top + p.height * (t + 1) / tramos + 0.6f
                val k = if (a.lumbre) cerca(fx, fy((ya + yb) / 2), n, a) else dia(fx, fy((ya + yb) / 2)) * 0.3f + 0.7f
                drawRect(a.luz(if (a.lumbre) PaletaDeco.MACASAR else mezcla(PaletaDeco.MACASAR, SOMBRA_MUEBLE, 0.4f * (1f - k)), k), Offset(p.left, ya), Size(p.width, yb - ya))
                val ia = maxOf(ya, piel.top); val ib = minOf(yb, piel.bottom)
                if (ib > ia) drawRect(a.luz(if (a.lumbre) PaletaDeco.GALUCHAT else mezcla(PaletaDeco.GALUCHAT, PaletaDeco.GALUCHAT_OSC, 0.6f * (1f - k)), k), Offset(piel.left, ia), Size(piel.width, ib - ia))
            }
            // El grano de la piel: perlas chicas por todo el paño, una sola llamada; claras arriba, sombra abajo.
            clipRect(piel.left, piel.top, piel.right, piel.bottom) {
                val puntos = ArrayList<Offset>(220)
                val paso = maxOf(2.2f * u, piel.width / 9f)
                var y = piel.top + paso * 0.5f
                var fila2 = 0
                while (y < piel.bottom && puntos.size < 1400) {
                    var x = piel.left + (if (fila2 % 2 == 0) 0f else paso * 0.5f)
                    while (x < piel.right) { puntos.add(Offset(x + (azar(941 + i, puntos.size) - 0.5f) * paso * 0.5f, y)); x += paso }
                    y += paso * 0.85f; fila2++
                }
                drawPoints(puntos, PointMode.Points, a.claro(0.55f).copy(alpha = if (a.oscuro) 0.12f else 0.5f), paso * 0.5f, StrokeCap.Round)
                drawPoints(puntos.map { Offset(it.x + paso * 0.12f, it.y + paso * 0.14f) }, PointMode.Points, a.sombra(0.16f), paso * 0.3f, StrokeCap.Round)
                // La estrella: perlas gruesas en rombo, cada tanto a lo alto.
                val paso2 = maxOf(40f * u, fila * 1.2f)
                var ye = piel.top + paso2 * 0.5f
                while (ye < piel.bottom) {
                    val c = Offset(piel.center.x, ye)
                    val gordas = listOf(c, Offset(c.x - paso, c.y), Offset(c.x + paso, c.y), Offset(c.x, c.y - paso), Offset(c.x, c.y + paso))
                    drawPoints(gordas, PointMode.Points, a.claro(0.7f).copy(alpha = if (a.oscuro) 0.16f else 0.7f), paso * 0.85f, StrokeCap.Round)
                    ye += paso2
                }
            }
            // El filete de marfil alrededor de la piel, el canto del marco con su luz y su sombra.
            drawRect(a.luz(PaletaDeco.MARFIL, if (a.lumbre) cerca(fx, fy(p.center.y), n, a) * 0.8f else 1f).copy(alpha = 0.8f), piel.topLeft, piel.size, style = Stroke(1f * u))
            drawRect(a.sombra(0.3f), Offset(piel.left - 1.6f * u, piel.top), Size(1.2f * u, piel.height))
            val luzIzq = i == 1 || !a.aplique || a.alba
            drawRect(a.claro(0.3f), Offset(if (luzIzq) p.left else p.right - 1.6f * u, p.top), Size(1.6f * u, p.height))
            drawRect(a.sombra(0.3f), Offset(if (luzIzq) p.right - 2f * u else p.left, p.top), Size(2f * u, p.height))
            if (a.aplique && i == 1) drawRect(a.filo(0.35f), Offset(p.left, p.top), Size(1.4f * u, p.height * 0.5f))
        }
    }

    /**
     * Una tabla vista por su canto: Macasar con la veta a lo largo, un filete de marfil cerca del borde de arriba y su sombra en
     * el casillero de abajo. De noche, por tramos.
     */
    private fun DrawScope.tablaDeco(r: Rect, k: Int, a: AireDeco, cerca: (Offset) -> Float) {
        val u = density
        drawRect(a.sombra(if (a.lumbre) 0.4f else 0.26f), Offset(r.left, r.bottom), Size(r.width, 3f * u))
        val tramos = if (a.lumbre) 20 else 1
        for (t in 0 until tramos) {
            val xa = r.left + r.width * t / tramos; val xb = r.left + r.width * (t + 1) / tramos + 0.6f
            val kk = cerca(Offset((xa + xb) / 2, r.center.y))
            drawRect(a.luz(PaletaDeco.MACASAR, kk), Offset(xa, r.top), Size(xb - xa, r.height))
            if (a.lumbre) drawRect(a.filo(0.05f + 0.4f * kk), Offset(xa, r.top), Size(xb - xa, 1f * u))
        }
        // Las vetas a lo largo: negras y de miel.
        val vetas = Path()
        for (j in 0 until 4) {
            val y = r.top + r.height * (0.45f + 0.13f * j)
            vetas.moveTo(r.left + r.width * azar(951 + k, j) * 0.2f, y)
            vetas.cubicTo(r.left + r.width * 0.35f, y + (azar(952 + k, j) - 0.5f) * 3f * u, r.left + r.width * 0.65f, y - (azar(953 + k, j) - 0.5f) * 3f * u, r.right, y)
        }
        drawPath(vetas, a.luz(PaletaDeco.MACASAR_NEGRO, 0.6f).copy(alpha = 0.6f), style = Stroke(1.2f * u))
        drawLine(a.luz(PaletaDeco.MACASAR_MIEL, cerca(r.center)).copy(alpha = 0.5f), Offset(r.left, r.top + r.height * 0.78f), Offset(r.right, r.top + r.height * 0.76f), 0.8f * u)
        // El filete de marfil y el canto redondeado.
        drawRect(a.luz(PaletaDeco.MARFIL, cerca(r.center)).copy(alpha = 0.85f), Offset(r.left, r.top + r.height * 0.22f), Size(r.width, 1.1f * u))
        drawRect(a.claro(0.35f), r.topLeft, Size(r.width, r.height * 0.16f))
        drawRect(a.sombra(0.28f), Offset(r.left, r.bottom - r.height * 0.22f), Size(r.width, r.height * 0.22f))
    }

    /** El rótulo de un mes: una placa de marfil con las esquinas cortadas en escalón y un marco de níquel, atornillada al canto. */
    private fun DrawScope.rotuloDeco(p: PlacaEstante, medidor: TextMeasurer, a: AireDeco, cerca: Float) {
        val u = density
        val r = Rect(p.r.left, p.r.top - 0.5f * u, p.r.right, p.r.bottom + 0.5f * u)
        if (r.width <= 2f || r.height <= 2f) return
        val e = minOf(r.height * 0.3f, 3f * u)
        val forma = escalonDeco(r, e)
        drawPath(TrazoDeco.corrido(forma, 1.2f * u, 1.4f * u), a.sombra(0.4f))
        drawPath(forma, a.luz(PaletaDeco.NIQUEL, maxOf(cerca, 0.3f)))
        val dentro = escalonDeco(Rect(r.left + 1.2f * u, r.top + 1.2f * u, r.right - 1.2f * u, r.bottom - 1.2f * u), e * 0.7f)
        drawPath(dentro, a.etiqueta(maxOf(cerca, 0.25f)))
        drawLine(a.claro(0.5f), Offset(r.left + e, r.top + 0.6f * u), Offset(r.right - e, r.top + 0.6f * u), 0.7f * u)
        // Los dos tornillos de níquel.
        for (x in listOf(r.left + e * 0.9f, r.right - e * 0.9f)) drawCircle(a.luz(PaletaDeco.NIQUEL_OSC, maxOf(cerca, 0.3f)), 0.9f * u, Offset(x, r.center.y))
        textoEn(medidor, p.texto, Rect(r.left + e * 1.8f, r.top + 1f * u, r.right - e * 1.8f, r.bottom - 1f * u), LETRA, a.tinta, 10f, peso = FontWeight.SemiBold, espaciado = 1f, minimo = 5.5f)
    }

    /** Un rectángulo con las cuatro esquinas cortadas en escalón, como las placas y las etiquetas déco. */
    internal fun escalonDeco(r: Rect, e: Float): Path = Path().apply {
        val m = e * 0.5f
        moveTo(r.left + e, r.top); lineTo(r.right - e, r.top); lineTo(r.right - e, r.top + m); lineTo(r.right - m, r.top + m)
        lineTo(r.right - m, r.top + e); lineTo(r.right, r.top + e); lineTo(r.right, r.bottom - e); lineTo(r.right - m, r.bottom - e)
        lineTo(r.right - m, r.bottom - m); lineTo(r.right - e, r.bottom - m); lineTo(r.right - e, r.bottom); lineTo(r.left + e, r.bottom)
        lineTo(r.left + e, r.bottom - m); lineTo(r.left + m, r.bottom - m); lineTo(r.left + m, r.bottom - e); lineTo(r.left, r.bottom - e)
        lineTo(r.left, r.top + e); lineTo(r.left + m, r.top + e); lineTo(r.left + m, r.top + m); lineTo(r.left + e, r.top + m); close()
    }

    /**
     * La tapa del armario: una losa de Macasar que vuela sobre los costados, con el canto redondeado (brillo arriba, sombra abajo),
     * un filete de marfil y su cara de arriba apenas vista; deja su sombra en la primera repisa. De noche, por tramos.
     */
    private fun DrawScope.tapaDeco(r: Rect, a: AireDeco, cerca: (Offset) -> Float) {
        val u = density; val w = size.width
        val vuelo = w * 0.004f
        val losa = Rect(vuelo, r.top, w - vuelo, r.bottom)
        drawRect(a.sombra(if (a.oscuro) 0.5f else 0.36f), Offset(0f, r.bottom), Size(w, 4f * u))
        drawRect(a.sombra(0.16f), Offset(0f, r.bottom + 4f * u), Size(w, 7f * u))
        val cara = r.height * 0.22f
        val tramos = if (a.lumbre) 24 else 1
        for (t in 0 until tramos) {
            val xa = losa.left + losa.width * t / tramos; val xb = losa.left + losa.width * (t + 1) / tramos + 0.6f
            val k = cerca(Offset((xa + xb) / 2, r.top))
            drawRect(a.luz(mezcla(PaletaDeco.MACASAR, PaletaDeco.MACASAR_MIEL, 0.25f), k), Offset(xa, losa.top), Size(xb - xa, cara))
            drawRect(a.luz(PaletaDeco.MACASAR, k), Offset(xa, losa.top + cara), Size(xb - xa, losa.height - cara))
            if (a.lumbre) drawRect(a.filo(0.08f + 0.55f * k), Offset(xa, losa.top + cara), Size(xb - xa, 1.2f * u))
        }
        val vetas = Path()
        for (j in 0 until 5) {
            val y = losa.top + cara + (losa.height - cara) * (0.15f + 0.17f * j)
            vetas.moveTo(losa.left, y)
            vetas.cubicTo(w * 0.3f, y + (azar(961, j) - 0.5f) * 4f * u, w * 0.7f, y + (azar(962, j) - 0.5f) * 4f * u, losa.right, y + (azar(963, j) - 0.5f) * 2f * u)
        }
        drawPath(vetas, a.luz(PaletaDeco.MACASAR_NEGRO, 0.6f).copy(alpha = 0.55f), style = Stroke(1.4f * u))
        drawRect(a.claro(0.42f), Offset(losa.left, losa.top + cara), Size(losa.width, 1.6f * u))
        drawRect(a.luz(PaletaDeco.MARFIL, cerca(Offset(w * 0.5f, r.center.y))).copy(alpha = 0.85f), Offset(losa.left, losa.top + cara + (losa.height - cara) * 0.55f), Size(losa.width, 1.2f * u))
        drawRect(a.sombra(0.32f), Offset(losa.left, losa.bottom - 2.2f * u), Size(losa.width, 2.2f * u))
        drawLine(a.sombra(0.4f), Offset(losa.left, losa.top), Offset(losa.right, losa.top), 0.7f * u)
        // El reflejo largo de la goma laca en la cara de arriba.
        if (!a.oscuro) drawRect(Color.White.copy(alpha = 0.16f), Offset(w * 0.08f, losa.top + cara * 0.3f), Size(w * 0.5f, cara * 0.35f))
        // Un cerco de vaso y un rayón.
        drawOval(a.sombra(0.14f), Offset(w * 0.68f, losa.top + cara * 0.2f), Size(w * 0.05f, cara * 0.6f), style = Stroke(0.8f * u))
    }

    // ---------------------------------------------------------------- cada entrada

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width <= 1f || size.height <= 1f) return
        val a = AireDeco(oscuro, v.noche)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val fy = CORONA + (v.repisa * FILA + ALTO_REPISA.value * 0.55f) / ANCHO_NOMINAL
        val k = if (a.lumbre) cerca(fx, fy, v.repisas, a) else dia(fx, fy) * 0.25f + 0.75f
        val izq = luzIzquierda(a, fx)
        when (clase(v.semilla)) {
            0 -> rolloDeco(v, medidor, a, k, izq)
            1 -> albumDeco(v, medidor, a, k, izq)
            else -> carpetaDeco(v, medidor, a, k, izq)
        }
        // De noche, el calor del aplique en los que le quedan cerca (arriba a la derecha).
        if (a.aplique) {
            val dx = fx - APL_X; val dy = fy - (CORONA + 0.03f)
            val kc = (1f - sqrt(dx * dx + dy * dy) / 0.5f).coerceIn(0f, 1f).pow(1.4f)
            if (kc > 0.01f) drawRect(PaletaDeco.APLIQUE.copy(alpha = (if (a.alba) 0.2f else 0.42f) * kc), Offset.Zero, size, blendMode = BlendMode.Screen)
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
}
