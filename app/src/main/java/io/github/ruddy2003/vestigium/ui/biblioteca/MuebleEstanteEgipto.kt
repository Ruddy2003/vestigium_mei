package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import kotlin.math.roundToInt
import androidx.compose.ui.unit.Density
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.sqrt

/**
 * La paleta del almacén de la Tumba (Deir el-Medina, hacia 1200 a. C.), de la misma casa que el Ideario: el enlucido de
 * barro dorado con su zócalo ocre y la franja roja, negra y azul; el adobe de las roscas; la palmera de los postes, la
 * acacia de los tablones y la soga de fibra; el papiro, el lino y el yeso; la marga de las tinajas con su azul; el bronce, la
 * fayenza y el grano. La sombra es violeta tostado (`sombraMueble`), la luz, crema.
 */
internal object EgiptoTinta {
    /** El enlucido de barro: su luz, su tono y su sombra; el sol que entra por la puerta, casi blanco. */
    val ENLUCIDO_LUZ = Color(0xFFF4E3BE)
    val ENLUCIDO = Color(0xFFE2C392)
    val ENLUCIDO_OSC = Color(0xFFB98F62)
    val SOL = Color(0xFFFFF6E2)
    /** El fondo de cada tramo: el mismo barro a la sombra del anaquel, hondo, para que resalte lo guardado. */
    val HONDO = Color(0xFFA38672)
    /** El fondo de los tramos: el barro sin enlucir del testero detrás del anaquel, un pardo rojizo hondo que hace resaltar lo guardado. */
    val TRAMO = Color(0xFF8A5C40)
    /** El zócalo ocre de la casa y el adobe que asoma. */
    val OCRE = Color(0xFFC47E3A)
    val ADOBE = Color(0xFF8C6A48)
    val PAJA = Color(0xFFE0C38A)
    /** La franja pintada: rojo, negro carbón y azul egipcio; el verde de la fayenza y el amarillo. */
    val ROJO = Color(0xFFAE3F28)
    val CARBON = Color(0xFF2B2422)
    val AZUL = Color(0xFF2C6CA8)
    val FAYENZA = Color(0xFF2A9A98)
    val AMARILLO = Color(0xFFE0B03A)
    /** El tronco de palmera de los postes: su luz, su tono y su sombra; la soga de fibra de palma. */
    val PALMA_LUZ = Color(0xFFD2A974)
    val PALMA = Color(0xFFA67C50)
    val PALMA_OSC = Color(0xFF4C3424)
    val FIBRA = Color(0xFFC9A066)
    /** Los tablones de acacia, desbastados a azuela. */
    val ACACIA_LUZ = Color(0xFFD6A46A)
    val ACACIA = Color(0xFFAF7846)
    val ACACIA_OSC = Color(0xFF6A4428)
    /** El papiro, el lino crudo, el yeso de las tablillas y la tinta negra; el rojo de las rúbricas es [ROJO]. */
    val PAPIRO = Color(0xFFEAD8A6)
    val PAPIRO_OSC = Color(0xFFB59A68)
    val LINO = Color(0xFFEDE2C6)
    val YESO = Color(0xFFF7F1E2)
    val TINTA = Color(0xFF1E160C)
    /** El barro de los sellos y de la lámpara; la marga crema de las tinajas. */
    val BARRO = Color(0xFFA65C38)
    val MARGA = Color(0xFFE6D1A4)
    /** El bronce de la balanza y su brillo, el cobre de los cinceles y su verdín; la piedra gris de las pesas. */
    val BRONCE = Color(0xFFC08A3A)
    val BRONCE_LUZ = Color(0xFFF2D58C)
    val COBRE = Color(0xFFC06E3C)
    val VERDIN = Color(0xFF5E9C88)
    val PIEDRA = Color(0xFF8F8A82)
    /** El trigo de las raciones, el pan cocido y la piel de las cebollas; la tierra apisonada del suelo. */
    val GRANO = Color(0xFFDDB458)
    val PAN = Color(0xFFC98A45)
    val CEBOLLA = Color(0xFFB8644A)
    val TIERRA = Color(0xFFB8946C)

    /** Los colores del ánimo, los del mundo, en el orden de la escala: ocre rojo, ocre amarillo, lino crudo (el neutro), verde de fayenza y azul egipcio. */
    val ANIMO = listOf(Color(0xFFA9402A), Color(0xFFCF9636), Color(0xFFDCCBA2), Color(0xFF2A9785), Color(0xFF2A5FAA))

    // Las luces: la noche del almacén (pardo violácea y cálida), su sombra, la de los Sueños, el atardecer, la llama y la luna.
    val NOCHE = Color(0xFF2C2030)
    val NOCHE_SOMBRA = Color(0xFF150E18)
    val NOCHE_SUENOS = Color(0xFF221D36)
    val ATARDECER = Color(0xFF6E4E7E)
    val OCASO = Color(0xFFFF9A52)
    val LLAMA = Color(0xFFFFC56E)
    val LUNA = Color(0xFFA9C0F2)
}

/**
 * El aire del momento en el almacén. De día, el sol que entra por la puerta (a la izquierda, fuera del cuadro) y deja su
 * mancha recortada; de noche (el modo oscuro) arden dos llamas, la lámpara de la repisa alta y la del suelo: cada cosa va de
 * su color hondo (un pardo violáceo cálido, de su mismo tono) a su color según lo que le llega de las dos, y la luna, que
 * entra por la misma puerta, deja solo una mancha chica de borde blando y un filo frío; en los Sueños ([noche]) en claro, el
 * sol ya bajo, anaranjado, con el resto en sombra lila; en oscuro ([lunar]), las dos llamas arden bajas y manda la luna.
 * [k] es cuánto llega de las llamas (de 0 a 1; de día, 1) y [f], de la puerta (el sol, el ocaso o la luna).
 */
internal class AireEgipto(val oscuro: Boolean, val noche: Boolean) {
    val lumbre: Boolean get() = oscuro || noche
    val lunar: Boolean get() = oscuro && noche
    val ocaso: Boolean get() = noche && !oscuro
    private val noct: Color get() = if (lunar) EgiptoTinta.NOCHE_SUENOS else EgiptoTinta.NOCHE

    /** El color propio de algo, hundido en la noche: oscuro y de su mismo tono, en el pardo violáceo del almacén. */
    private fun hondo(c: Color) = Color(c.red * 0.16f + noct.red * 0.78f, c.green * 0.16f + noct.green * 0.78f, c.blue * 0.16f + noct.blue * 0.78f, c.alpha)

    /** Lo que toca la luna: el color de la cosa, apagado y enfriado (con su tono, no un gris). */
    private fun lunado(c: Color) = Color(c.red * 0.4f + 0.06f, c.green * 0.46f + 0.08f, c.blue * 0.55f + 0.2f, c.alpha)

    /** El color de algo con la luz que le llega: [k] de las llamas, [f] de la puerta. */
    fun c(col: Color, k: Float, f: Float = 0f): Color {
        if (!oscuro) {
            if (!noche) return col
            return mezcla(mezcla(col, EgiptoTinta.ATARDECER, 0.3f * (1f - f)), EgiptoTinta.OCASO, 0.16f * f + 0.08f * k)
        }
        val kk = k.coerceIn(0f, 1f)
        var v = mezcla(hondo(col), col, (1.02f * kk).coerceAtMost(0.95f))
        v = mezcla(v, EgiptoTinta.LLAMA, 0.22f * kk)
        val ff = f.coerceIn(0f, 1f) * (1f - kk) * (if (lunar) 0.66f else 0.32f)
        return if (ff > 0f) mezcla(v, lunado(col), ff) else v
    }
    /** La sombra: de día violeta tostado; de noche, la noche del almacén. */
    fun s(a: Float): Color = when {
        oscuro -> EgiptoTinta.NOCHE_SOMBRA.copy(alpha = (a * 1.3f).coerceIn(0f, 1f))
        noche -> sombraMueble((a * 1.2f).coerceIn(0f, 1f))
        else -> sombraMueble(a)
    }
    /** El brillo de lo que mira a la luz: de día crema; de noche, el filo de la llama según [k]. */
    fun b(a: Float, k: Float = 1f): Color = when {
        oscuro -> EgiptoTinta.LLAMA.copy(alpha = (a * 0.95f * k).coerceIn(0f, 1f))
        noche -> luzMueble(a * 0.7f)
        else -> luzMueble(a)
    }
    /** El filo frío de la luna en lo que mira a la puerta (de noche). */
    fun helado(a: Float, f: Float): Color = EgiptoTinta.LUNA.copy(alpha = if (oscuro) (a * f).coerceIn(0f, 1f) else 0f)
    /** La luz de la puerta sobre lo que toca: el sol (de día), el ocaso (Sueños en claro) o la luna (de noche, apenas). */
    fun puerta(a: Float): Color = when {
        !lumbre -> EgiptoTinta.SOL.copy(alpha = a.coerceIn(0f, 1f))
        !oscuro -> EgiptoTinta.OCASO.copy(alpha = (a * 0.8f).coerceIn(0f, 1f))
        else -> EgiptoTinta.LUNA.copy(alpha = (a * (if (lunar) 0.4f else 0.26f)).coerceIn(0f, 1f))
    }
    /** El color del ánimo: de noche se apaga con la distancia a las llamas, sin perder su tinte. */
    fun animo(t: Color, k: Float, f: Float = 0f): Color {
        if (!oscuro) return c(t, k, f)
        val kk = k.coerceIn(0f, 1f)
        var v = mezcla(Color(t.red * 0.44f + noct.red * 0.42f, t.green * 0.44f + noct.green * 0.42f, t.blue * 0.44f + noct.blue * 0.42f), t, (1.08f * kk).coerceAtMost(0.96f))
        v = mezcla(v, EgiptoTinta.LLAMA, 0.16f * kk)
        val ff = f.coerceIn(0f, 1f) * (1f - kk) * (if (lunar) 0.45f else 0.3f)
        return if (ff > 0f) mezcla(v, lunado(t), ff) else v
    }
    /** El papiro o el yeso donde va el día: casi no se apaga, para que el número se lea siempre (4.5 a 1 o más). */
    fun etiqueta(k: Float, base: Color = EgiptoTinta.YESO): Color {
        val kk = k.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(base, EgiptoTinta.ATARDECER, 0.12f) else base
        val lejos = mezcla(base, Color(0xFFA69A9C), if (lunar) 0.58f else 0.5f)
        return mezcla(lejos, mezcla(base, EgiptoTinta.LLAMA, 0.14f), (1.3f * kk).coerceAtMost(1f))
    }
    val tinta: Color get() = EgiptoTinta.TINTA
    val rubrica: Color get() = if (oscuro) mezcla(EgiptoTinta.ROJO, EgiptoTinta.TINTA, 0.2f) else EgiptoTinta.ROJO
}

/**
 * El dibujo del estante del diario de Egipto (lo usa `EstanteEgipto`, en `BibliotecaAntiguedad.kt`): el almacén de la Tumba,
 * donde Pay, escriba de la cuadrilla, guarda sus cuentas junto a lo que reparte. Una nave enlucida con bóveda de cañón de
 * ladrillo (sus roscas a la vista y la franja pintada siguiendo el arco), con el zócalo ocre de la casa; contra el testero, un
 * anaquel de dos troncos de palmera hincados en el suelo, con tablones gruesos de acacia apoyados en estacas y atados con
 * soga de fibra. Arriba, el bodegón: la balanza del cobre, las tinajas pintadas, el cesto de los panes; colgadas, la hoz y
 * las cebollas. Al pie, la estera, el costal, la medida de grano, la tinaja en su soporte y la lámpara. El muro y el suelo van
 * en `MuebleEstanteEgiptoMuro.kt`; el anaquel, en `MuebleEstanteEgiptoAnaquel.kt`; lo de arriba y del pie, en
 * `MuebleEstanteEgiptoPiezas.kt` y `MuebleEstanteEgiptoPie.kt`; los volúmenes, en `MuebleEstanteEgiptoVolumenes.kt`.
 */
internal object MuebleEstanteEgipto {
    val LETRA: FontFamily = Letra.messiri

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.62f
    const val BASE = 0.36f
    const val LADO = 0.085f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 16.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (26 + (palabras / 30).coerceAtMost(20)).dp

    /** Cuál de los tres es una entrada: 0, el rollo en su funda de lino; 1, la tablilla de madera pintada; 2, el fardo de lino. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla, 93)
        return if (x < 0.4f) 0 else if (x < 0.7f) 1 else 2
    }

    /** El rollo, el más alto; la tablilla y el fardo, poco menos. Todos llenan casi el tramo (del 84 al 95 %). */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.88f + 0.07f * azar(semilla, 1)
        1 -> 0.85f + 0.06f * azar(semilla, 1)
        else -> 0.84f + 0.06f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde la lámpara del suelo, en anchos del mueble: [LAMP_X] desde la izquierda y [LAMP_BAJO] por debajo del último tablón. */
    const val LAMP_X = 0.7f
    const val LAMP_BAJO = 0.075f
    /** Dónde arde la lámpara de la repisa alta: [ALTA_X] desde la izquierda y [ALTA_SOBRE] por encima del borde de la corona. */
    const val ALTA_X = 0.38f
    const val ALTA_SOBRE = 0.165f
    /** La mancha de la luna (de noche), en anchos desde la esquina de arriba a la izquierda. */
    const val LUNA_X = 0.14f
    const val LUNA_Y = 0.3f
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 128f
    /** En la base: dónde empieza el suelo, en fracción de su alto. */
    const val SUELO = 0.4f

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireEgipto(oscuro, noche)
        val ch = d.corona.bottom
        val b = d.base
        val suelo = b.top + b.height * SUELO
        val llama = Offset(w * LAMP_X, b.top + w * LAMP_BAJO)
        val fondo = d.tablas.lastOrNull()?.bottom ?: ch
        val m = MesaEgipto(a, w, ch, suelo, llama, h)
        val clave = "$w|$ch|$suelo|${b.top}|${b.height}|$h|$density|$oscuro|$noche"
        val ranura = if (noche) 2 else 0
        // ---- arriba (pintado una vez por tamaño y modo): el testero, la bóveda, lo colgado, el tablón alto y el bodegón
        val corona: DrawScope.() -> Unit = {
            muroEgipto(m, listOf(Rect(0f, 0f, w, ch)))
            muroDetallesEgipto(m)
            bovedaEgipto(m)
            colgadoEgipto(m)
            postesEgipto(m)
            arribaEgipto(m)
            bodegonEgipto(m)
            if (a.lumbre) llamaAltaEgipto(m)
            granoMueble(Rect(0f, 0f, w, ch), oscuro)
        }
        // Si el borde de la corona (ch) cae a medio píxel, la última fila de su imagen quedaba a medias y el cuerpo empieza en el
        // mismo borde: entre los dos asomaba el fondo, una línea a todo el ancho. Por eso la imagen llega hasta el píxel entero
        // de abajo (chI) y, debajo de esa fila, va la corona corrida un píxel (su fila de encima, entera): queda cubierta.
        val chI = ceil(ch)
        val debajo: (DrawScope.() -> Unit)? = if (chI > ch && chI >= 2f) {
            { clipRect(0f, chI - 1f, w, chI) { translate(0f, 1f) { corona() } } }
        } else null
        with(CapaEgipto) { pegar(ranura, clave, Rect(0f, 0f, w, ch), debajo = debajo, abajo = chI, pintar = corona) }
        // ---- el cuerpo (cambia con lo guardado): el fondo de cada tramo, los postes, los tablones y lo de los tramos libres
        clipRect(0f, ch, w, fondo) {
            muroEgipto(m, listOf(Rect(0f, ch, m.posteI.right, fondo), Rect(m.posteD.left, ch, w, fondo)))
            // Las juntas de cada tablón con el tramo de debajo, que pueden caer a medio píxel: debajo de los dos, esa fila.
            d.repisas.forEachIndexed { i, r -> if (i > 0) filaJuntaEgipto(m, r.top, m.posteI.right, m.posteD.left) }
            fondosTramosEgipto(m, d.repisas)
            d.repisas.forEach { r -> fondoTramoEgipto(m, r) }
            postesEgipto(m)
        }
        // La junta del último tablón con el pie (que se pega después), a lo ancho del tablón: debajo de los dos, esa fila.
        val ultima = d.tablas.size - 1
        if (ultima >= 0) filaJuntaEgipto(m, fondo, w * (0.003f + 0.004f * azar(ultima, 3)), w * (0.997f - 0.004f * azar(ultima, 4)))
        anaquelEgipto(m, d)
        d.placas.forEach { p -> rotuloEgipto(m, p, medidor) }
        d.repisas.forEachIndexed { i, r -> d.ocupado.getOrNull(i)?.let { oc -> huecoLibreEgipto(m, i, r, oc) } }
        granoMueble(Rect(0f, ch, w, fondo), oscuro)
        // ---- abajo (pintado una vez): el pie del muro, el zócalo, el suelo con la estera y lo del suelo con su lámpara
        with(CapaEgipto) {
            pegar(ranura + 1, clave, Rect(0f, fondo, w, h)) {
                muroEgipto(m, listOf(Rect(0f, fondo, w, suelo)))
                sueloEgipto(m, b)
                postesEgipto(m)
                pieEgipto(m)
                if (a.lumbre) llamaEgipto(m)
                granoMueble(Rect(0f, fondo, w, h), oscuro)
            }
        }
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) = volumenEgipto(v, medidor, oscuro)
}

/**
 * Lo que comparten las piezas del estante en un dibujo: el aire, el ancho, dónde acaba la corona, dónde empieza el suelo, las
 * dos llamas y, ya armados, la forma de la mancha de la puerta y las medidas del anaquel.
 */
internal class MesaEgipto(val a: AireEgipto, val w: Float, val ch: Float, val suelo: Float, val llama: Offset, val alto: Float) {
    /** La llama de la lámpara de la repisa alta. */
    val llamaAlta = Offset(w * MuebleEstanteEgipto.ALTA_X, ch - w * MuebleEstanteEgipto.ALTA_SOBRE)
    /** La mancha de la puerta en el muro, en píxeles (de día el sol; en los Sueños claros, el ocaso). */
    val puerta: Path = formaPuertaEgipto(w, a.ocaso)
    /** La parte de la mancha de la puerta que cae en [r] (un camino ya cortado: pintarlo es más barato que recortar). */
    fun puertaEn(r: Rect): Path = Path().apply { op(puerta, Path().apply { addRect(r) }, PathOperation.Intersect) }
    /** Los postes: el de la izquierda y el de la derecha (el tronco entero, del corte de arriba a donde se hinca). */
    val posteI = Rect(w * 0.012f, ch - w * 0.13f, w * 0.084f, suelo + w * 0.035f)
    val posteD = Rect(w * 0.916f, ch - w * 0.122f, w * 0.988f, suelo + w * 0.04f)
    /** El tablón de arriba: su cara de arriba (vista un poco desde arriba) y su frente. */
    val caraArriba = Rect(-w * 0.004f, ch - w * 0.105f, w * 1.004f, ch - w * 0.075f)
    val frenteArriba = Rect(0f, ch - w * 0.075f, w, ch)
    /** Dónde pisa lo de arriba: el medio de la cara del tablón. */
    val apoyo: Float get() = caraArriba.center.y + w * 0.004f

    /** Lo que llega de la lámpara del suelo y de la de arriba a un punto, cada una por separado. */
    fun k1(p: Offset): Float = lamparaLuzEgipto((p.x - llama.x) / w, (p.y - llama.y) / w, a.lunar)
    fun k2(p: Offset): Float = lamparaAltaLuzEgipto((p.x - llamaAlta.x) / w, (p.y - llamaAlta.y) / w, a.lunar)
    /** Cuánto llega de las dos llamas a un punto (de día, todo). */
    fun k(p: Offset): Float = if (a.lumbre) juntarLucesEgipto(k1(p), k2(p)) else 1f
    /** Cuánto llega de la puerta (el sol de día, el ocaso en los Sueños claros, la luna de noche). */
    fun f(p: Offset): Float = if (a.oscuro) lunaEgipto(p.x / w, p.y / w, a.lunar) else luzPuertaEgipto(p.x / w, p.y / w, a.ocaso)
    /** Lo de arriba nunca se pierde del todo. */
    fun kAlto(p: Offset): Float = maxOf(k(p), if (a.oscuro) 0.05f else 0f)
    /** De noche, de qué lado le viene la luz a algo en [x], [y]: de la llama que más le alumbra. */
    fun der(x: Float, y: Float): Boolean {
        if (!a.oscuro) return false
        val p = Offset(x, y)
        val lx = if (k2(p) > k1(p)) llamaAlta.x else llama.x
        return x < lx - w * 0.02f
    }
    /** La llama que más alumbra un punto (para las sombras de noche). */
    fun llamaDe(p: Offset): Offset = if (k2(p) > k1(p)) llamaAlta else llama
}

// ---------------------------------------------------------------- la luz, compartida por el mueble y los volúmenes

/**
 * Cuánta luz de la lámpara del suelo llega (de 1 a 0) a un punto a [dx] y [dy] anchos de la llama: cae con la distancia, como
 * la del Ideario, y se acaba del todo a su alcance (así lo lejano queda en su color hondo y el degradado se pinta solo donde
 * llega). Con [corto] (los Sueños de noche, la llama baja), el charco es más chico.
 */
internal fun lamparaLuzEgipto(dx: Float, dy: Float, corto: Boolean = false): Float {
    val d = sqrt(dx * dx + dy * dy)
    val (a, e, alcance) = if (corto) Triple(1.0f, 4.4f, 0.7f) else Triple(1.5f, 3.0f, 1.0f)
    val fin = a * exp(-e * alcance)
    return ((a * exp(-e * d) - fin) / (1f - fin)).coerceIn(0f, 1f)
}

/** Lo mismo para la lámpara de la repisa alta, más chica. */
internal fun lamparaAltaLuzEgipto(dx: Float, dy: Float, corto: Boolean = false): Float {
    val d = sqrt(dx * dx + dy * dy)
    val (a, e, alcance) = if (corto) Triple(1.0f, 4.6f, 0.55f) else Triple(1.5f, 3.0f, 0.85f)
    val fin = a * exp(-e * alcance)
    return ((a * exp(-e * d) - fin) / (1f - fin)).coerceIn(0f, 1f)
}

/** El alcance de cada llama, en anchos (donde su luz ya es cero). */
internal fun alcanceLlamaEgipto(alta: Boolean, corto: Boolean): Float = if (alta) (if (corto) 0.55f else 0.85f) else (if (corto) 0.7f else 1.0f)

/** Dos luces juntas: lo que no deja pasar ninguna de las dos. */
internal fun juntarLucesEgipto(k1: Float, k2: Float): Float = 1f - (1f - k1) * (1f - k2)

/** La luna (de noche): una mancha chica de borde blando, arriba a la izquierda, donde de día entra el sol; más grande en los Sueños. */
internal fun lunaEgipto(x: Float, y: Float, lunar: Boolean): Float {
    // En los Sueños la luna entra más honda y alcanza la primera repisa: manda ella.
    val dx = x - (if (lunar) MuebleEstanteEgipto.LUNA_X + 0.06f else MuebleEstanteEgipto.LUNA_X)
    val dy = y - (if (lunar) MuebleEstanteEgipto.LUNA_Y + 0.12f else MuebleEstanteEgipto.LUNA_Y)
    val r = if (lunar) 0.78f else 0.42f
    return ((1f - sqrt(dx * dx + dy * dy) / r) * (if (lunar) 1f else 0.85f)).coerceIn(0f, 1f)
}

/**
 * La mancha de la puerta en el testero, en anchos del mueble desde su esquina de arriba a la izquierda: un cuadrilátero con
 * el borde de arriba (el dintel) y el de la derecha (la jamba) recortados en diagonal. Con [ocaso] (los Sueños en claro), el
 * sol ya bajo: más larga y más baja.
 */
private fun puntosPuertaEgipto(ocaso: Boolean): FloatArray =
    if (ocaso) floatArrayOf(0f, 0.42f, 0.66f, 0.58f, 1.02f, 1.5f, 0f, 1.36f)
    else floatArrayOf(0f, 0.17f, 0.54f, 0.25f, 0.78f, MuebleEstanteEgipto.CORONA + 0.44f, 0f, MuebleEstanteEgipto.CORONA + 0.52f)

internal fun formaPuertaEgipto(w: Float, ocaso: Boolean): Path {
    val q = puntosPuertaEgipto(ocaso)
    return poligonoEgipto(-2f, q[1] * w, q[2] * w, q[3] * w, q[4] * w, q[5] * w, -2f, q[7] * w)
}

/** Cuánto da la luz de la puerta (de 0 a 1) en un punto a [x] e [y] anchos de la esquina: 1 dentro, con el borde un poco suave. */
internal fun luzPuertaEgipto(x: Float, y: Float, ocaso: Boolean): Float {
    val q = puntosPuertaEgipto(ocaso)
    if (x < 0f) return 0f
    val arriba = q[1] + (q[3] - q[1]) * x / q[2]
    val abajo = q[7] + (q[5] - q[7]) * x / q[4]
    val derecha = q[2] + (q[4] - q[2]) * ((y - q[3]) / (q[5] - q[3])).coerceIn(0f, 1f)
    return minOf((y - arriba) / 0.025f, (abajo - y) / 0.035f, (derecha - x) / 0.03f).coerceIn(0f, 1f)
}

/** La luz de las llamas, de la puerta y de qué lado le llega la de las llamas a un volumen (su repisa de cuántas y su centro). */
internal class LuzEnVolumenEgipto(val k: Float, val f: Float, val der: Boolean)

internal fun luzVolumenEgipto(v: Volumen, oscuro: Boolean): LuzEnVolumenEgipto {
    val m = MuebleEstanteEgipto
    val fx = m.LADO + v.centro * (1f - 2 * m.LADO)
    val y = m.CORONA + (v.repisa + 0.55f) * m.FILA / m.ANCHO_NOMINAL
    if (!oscuro && !v.noche) return LuzEnVolumenEgipto(1f, luzPuertaEgipto(fx, y, false), false)
    val lunar = oscuro && v.noche
    val desdeAbajo = ((v.repisas - v.repisa - 0.5f) * m.FILA) / m.ANCHO_NOMINAL + m.LAMP_BAJO
    val k1 = lamparaLuzEgipto(fx - m.LAMP_X, desdeAbajo, lunar)
    val k2 = lamparaAltaLuzEgipto(fx - m.ALTA_X, y - (m.CORONA - m.ALTA_SOBRE), lunar)
    val k = juntarLucesEgipto(k1, k2)
    val f = if (oscuro) lunaEgipto(fx, y, lunar) else luzPuertaEgipto(fx, y, true)
    val lx = if (k2 > k1) m.ALTA_X else m.LAMP_X
    return LuzEnVolumenEgipto(k, f, oscuro && fx < lx - 0.02f)
}

/** Las escalas del ánimo de Egipto, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_EGIPTO: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.EGIPTO, false) to juegoTonos(TemaId.EGIPTO, true) }

/**
 * El color del ánimo tal como es de día. En el modo oscuro la escala del tema cambia de colores (el neutro pasa del crema a
 * un casi negro, pensado para la interfaz) y un volumen claro se volvía negro de noche. Aquí el volumen es un objeto del
 * almacén: conserva su color de día y la luz lo apaga con la distancia a la lámpara. Se ubica el color en la escala de noche
 * (el tramo y la fracción que más se le acercan) y se toma el mismo punto en la de día; si no está en la escala, queda igual.
 */
internal fun tonoDeDiaEgipto(t: Color, oscuro: Boolean): Color {
    if (!oscuro) return t
    val (dia, noche) = TONOS_EGIPTO
    if (dia.size < 3 || noche.size < 3) return t
    var mejor = t
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = noche[seg]; val a1 = noche[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoEgipto(t.red - a0.red - dr * f) + cuadradoEgipto(t.green - a0.green - dg * f) + cuadradoEgipto(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = mezcla(dia[seg], dia[seg + 1], f) }
    }
    return mejor
}

private fun cuadradoEgipto(x: Float) = x * x

/**
 * El ánimo con los colores del mundo, con cuerpo: se ubica el color (ya de día) en la escala del tema (de bajo a alto, de 0
 * a 1, el neutro en 0.5) y se toma el mismo punto en la escala de [EgiptoTinta.ANIMO] (ocre rojo, ocre amarillo, lino crudo,
 * verde de fayenza, azul egipcio): el orden se conserva. Si el color no está en la escala, queda igual.
 */
internal fun animoMundoEgipto(t: Color): Color {
    val dia = TONOS_EGIPTO.first
    if (dia.size < 3) return t
    var mejor = -1f
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = dia[seg]; val a1 = dia[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoEgipto(t.red - a0.red - dr * f) + cuadradoEgipto(t.green - a0.green - dg * f) + cuadradoEgipto(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    if (mejor < 0f) return t
    val e = EgiptoTinta.ANIMO
    val x = mejor * (e.size - 1)
    val i = x.toInt().coerceIn(0, e.size - 2)
    return mezcla(e[i], e[i + 1], x - i)
}

// ---------------------------------------------------------------- lo fijo pintado una vez

/**
 * Lo fijo pintado una vez: cuatro imágenes (la franja de arriba y la de abajo, del diario y de los Sueños), cada una con la
 * clave de su tamaño y su modo; si la clave cambia, se vuelve a pintar. Pegar una imagen cuesta mucho menos que repetir los
 * degradados, las capas del muro y el bodegón en cada dibujo (como en Edo). Las franjas no crecen con las repisas.
 */
internal object CapaEgipto {
    private val claves = arrayOfNulls<String>(4)
    private val imagenes = arrayOfNulls<ImageBitmap>(4)

    /**
     * Pega la imagen de [r] (la pinta antes si su clave cambió). [debajo], si va, se pinta en una imagen que sigue hasta
     * [abajo], debajo de la de [r] tal cual: es lo que cubre la fila de la junta que [r] corta a medio píxel.
     */
    fun DrawScope.pegar(ranura: Int, clave: String, r: Rect, debajo: (DrawScope.() -> Unit)? = null, abajo: Float = r.bottom,
                        pintar: DrawScope.() -> Unit) {
        val ancho = r.width.roundToInt(); val alto = r.height.roundToInt()
        if (ancho < 1 || alto < 1 || ranura !in 0..3) return
        var img = imagenes[ranura]
        if (img == null || claves[ranura] != clave) {
            val base = ImageBitmap(ancho, alto)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(base), Size(ancho.toFloat(), alto.toFloat())) {
                translate(-r.left, -r.top) { clipRect(r.left, r.top, r.right, r.bottom) { pintar() } }
            }
            // Con [debajo]: otra imagen, hasta [abajo], con lo de debajo y encima la de arriba tal cual (la misma, píxel a píxel).
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

// ---------------------------------------------------------------- ayudantes de forma y de luz

internal fun poligonoEgipto(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

internal fun corridoEgipto(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

/**
 * El bulto de un cuerpo redondo dentro de [r] (se llama dentro de su recorte), como en el Ideario: el plano de luz, el brillo,
 * el plano de sombra y la sombra honda del otro lado. Con [der], la luz le llega por la derecha (de noche, de la lámpara).
 */
internal fun DrawScope.cilindroEgipto(r: Rect, a: AireEgipto, k: Float, fuerza: Float = 1f, der: Boolean = false, luna: Float = 0f) {
    fun x(f: Float, ancho: Float) = if (der) r.right - r.width * (f + ancho) else r.left + r.width * f
    drawRect(a.b(0.22f * fuerza, k), Offset(x(0f, 0.32f), r.top), Size(r.width * 0.32f, r.height))
    drawRect(a.b(0.34f * fuerza, k), Offset(x(0.13f, 0.09f), r.top), Size(r.width * 0.09f, r.height))
    drawRect(a.s(0.2f * fuerza), Offset(x(0.58f, 0.42f), r.top), Size(r.width * 0.42f, r.height))
    drawRect(a.s(0.16f * fuerza), Offset(x(0.86f, 0.14f), r.top), Size(r.width * 0.14f, r.height))
    // De noche, la luna que entra por la puerta (a la izquierda) le deja un filo frío del otro lado que la llama.
    if (a.oscuro && luna > 0.08f) {
        drawRect(a.helado(0.2f, luna), Offset(r.left, r.top), Size(r.width * 0.3f, r.height))
        drawRect(a.helado(0.34f, luna), Offset(r.left + r.width * 0.06f, r.top), Size(r.width * 0.08f, r.height))
    }
}

/** El filo cálido de la llama en el canto de arriba y en el costado que la mira, de noche; de día no pinta nada. */
internal fun DrawScope.filoEgipto(r: Rect, a: AireEgipto, k: Float, der: Boolean) {
    if (!a.oscuro || k < 0.06f) return
    val u = density
    drawRect(EgiptoTinta.LLAMA.copy(alpha = (0.85f * k).coerceAtMost(0.75f)), r.topLeft, Size(r.width, 1.4f * u))
    val x = if (der) r.right - 1.3f * u else r.left
    drawRect(EgiptoTinta.LLAMA.copy(alpha = (0.6f * k).coerceAtMost(0.55f)), Offset(x, r.top), Size(1.3f * u, r.height))
}

/** Un brillo puntual: de día crema; de noche, el de la llama si llega. */
internal fun DrawScope.destelloEgipto(c: Offset, r: Float, a: AireEgipto, k: Float) {
    if (a.oscuro) {
        if (k > 0.08f) { drawCircle(EgiptoTinta.LLAMA.copy(alpha = (0.3f * k).coerceAtMost(0.28f)), r * 2.2f, c); drawCircle(Color(0xFFFFF0CF).copy(alpha = (1.1f * k).coerceAtMost(0.9f)), r, c) }
    } else drawCircle(luzMueble(if (a.noche) 0.6f else 0.85f), r, c)
}

/** La sombra de contacto de lo que pisa algo, corrida a la derecha. */
internal fun DrawScope.contactoEgipto(c: Offset, ancho: Float, alto: Float, a: AireEgipto, fuerza: Float = 0.4f) =
    drawOval(a.s(fuerza), Offset(c.x - ancho * 0.42f, c.y - alto / 2), Size(ancho, alto))

/**
 * La sombra que algo echa en el muro de detrás: de día, corrida a la derecha y abajo (más honda dentro de la mancha del sol);
 * de noche, hacia fuera de la llama si la lámpara llega. [desde] es el centro de la cosa.
 */
internal fun DrawScope.sombraMuroEgipto(m: MesaEgipto, p: Path, desde: Offset, fuerza: Float = 0.2f) {
    val w = m.w
    val a = m.a
    if (!a.oscuro) {
        val sol = m.f(desde)
        translate(w * 0.026f, w * 0.013f) { drawPath(p, a.s(fuerza * (1f + 0.6f * sol))) }
    } else {
        // La sombra que echa la llama que más le alumbra, hacia fuera de ella (y hacia arriba si la llama está debajo).
        val k = m.k(desde)
        if (k > 0.1f) {
            val ll = m.llamaDe(desde)
            val dx = ((desde.x - ll.x) / w).coerceIn(-0.3f, 0.3f) / 0.3f
            val dy = if (ll.y > desde.y) -w * 0.016f else w * 0.01f
            translate(w * 0.045f * dx, dy) { drawPath(p, EgiptoTinta.NOCHE_SOMBRA.copy(alpha = (0.95f * k).coerceAtMost(0.55f))) }
        }
    }
}

