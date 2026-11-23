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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * La paleta de la cocina de la casa del almacén de El Remanso (Colonos, 2288): la cal azul de la pared de adobe, el barro que
 * asoma y el almagre del zócalo; el amarillo maíz de la alacena, el pino que asoma donde se gastó y el turquesa de dentro; el
 * nogal de la radio, el hierro, la lata, el peltre; las pinturas del pueblo que ya tiene la cajonera del Ideario (ladrillo,
 * marino, salvia); el crudo, el papel y el cordel; la milpa (girasol, calabaza, elote); el barro cocido del piso; y las luces.
 * Dos temperaturas: lo cálido es el maíz, el barro y las luces; lo frío, la cal azul, el turquesa y la luna.
 */
internal object PaletaColonos {
    /** La cal azul (con añil) de la pared: su cara, su luz y su sombra. */
    val CAL = Color(0xFF8DA9C4)
    val CAL_LUZ = Color(0xFFC7D7E3)
    val CAL_OSC = Color(0xFF5B7896)
    /** El barro del enjarre que asoma donde se cayó la cal, el adobe y el almagre del zócalo y de la cenefa. */
    val BARRO = Color(0xFFB98B5D)
    val ADOBE = Color(0xFF94633F)
    val ALMAGRE = Color(0xFF9A4431)
    /** El amarillo maíz de la alacena, su luz y su sombra; el pino que asoma donde se gastó la pintura. */
    val MAIZ = Color(0xFFBD8A2E)
    val MAIZ_LUZ = Color(0xFFD9AE58)
    val MAIZ_OSC = Color(0xFF85601B)
    val PINO = Color(0xFFCDA673)
    val PINO_OSC = Color(0xFF7C5833)
    /** El pino nuevo de la tabla repuesta, sin pintar, y el verde botica de la mano de pintura de antes. */
    val PINO_NUEVO = Color(0xFFE4C995)
    val VERDE_BOTICA = Color(0xFF4F7F62)
    /** El turquesa de dentro de la alacena, su luz y su hondo. */
    val TURQUESA = Color(0xFF368A80)
    val TURQUESA_LUZ = Color(0xFF62B3A6)
    val TURQUESA_OSC = Color(0xFF1C5751)
    /** De noche, el turquesa se vuelve verde botella, y lo que no alumbra la luz se hunde en un verde botella hondo. */
    val BOTELLA = Color(0xFF2B6E58)
    val BOTELLA_HONDO = Color(0xFF0B241E)
    /** El nogal de la radio, la viga, el hierro, la lata, su óxido y el peltre azul. */
    val NOGAL = Color(0xFF6C4128)
    val NOGAL_LUZ = Color(0xFFA06E45)
    val VIGA = Color(0xFF5B4331)
    val HIERRO = Color(0xFF2E2B2D)
    val HIERRO_LUZ = Color(0xFF7A7470)
    val LATA = Color(0xFFADB3B1)
    val LATA_OSC = Color(0xFF6A7271)
    val OXIDO = Color(0xFF9C5730)
    val PELTRE = Color(0xFF2D5C8E)
    /** Las pinturas del pueblo (las de la cajonera del Ideario), el crudo, el papel, la tinta y el cordel. */
    val LADRILLO = Color(0xFF9E2B25)
    val MARINO = Color(0xFF3E4B60)
    val SALVIA = Color(0xFF7F9068)
    val CRUDO = Color(0xFFE8DDC4)
    val PAPEL = Color(0xFFF1E8D3)
    val TINTA = Color(0xFF1E1A16)
    val CORDEL = Color(0xFFC9A86A)
    /** La milpa: el girasol, la calabaza y su verde, el elote. */
    val GIRASOL = Color(0xFFE6AC28)
    val CALABAZA = Color(0xFFD5772A)
    val VERDE = Color(0xFF5E7A3A)
    val ELOTE = Color(0xFFE8C45C)
    /** El barro cocido del piso, su luz y su junta. */
    val TEJA = Color(0xFFA65B3B)
    val TEJA_LUZ = Color(0xFFC57A52)
    val JUNTA = Color(0xFF64351F)
    /** Las luces: la noche del almacén (la del Ideario) y la de los Sueños; el anochecer y el poniente; la bombilla, la brasa,
     * la luna y el sol. */
    val NOCHE = Color(0xFF0C121E)
    val NOCHE_SUENOS = Color(0xFF0B1234)
    val ANOCHECER = Color(0xFF4A3B6C)
    val PONIENTE = Color(0xFFF2955A)
    val BOMBILLA = Color(0xFFFFC56A)
    val BRASA = Color(0xFFF2672E)
    val LUNA = Color(0xFFC2D2F2)
    val SOL = Color(0xFFFFF0CC)

    val LETRA: FontFamily = LetraExtra.fell
}

/**
 * El aire del momento en la cocina: de día (el sol de la ventana de botellas, arriba a la izquierda), de noche (el modo oscuro:
 * arde la bombilla del generador arriba a la derecha, con su cono hacia abajo; la brasa de la estufa al pie, a la izquierda; y
 * el candil de frasco colgado a media altura del costado izquierdo) y en los Sueños ([noche]: al anochecer en claro, con el
 * poniente por las botellas y la bombilla recién prendida; en oscuro, el generador al mínimo, la bombilla baja, la luna por
 * las botellas, el candil y la brasa). De noche cada color va de su hondo (oscuro, hacia el añil de la noche) a su color según lo
 * que le llega de las luces ([k], de 0 a 1), pasando por un oliva hondo en lo que queda a medias, y cerca de la luz se tiñe de
 * ámbar (o de naranja, en la parte [br] que viene de la brasa); en los Sueños de noche, lo que mira a la ventana se enfría con
 * la luna ([f]); al anochecer, [f] es el poniente.
 */
internal class AireColonos(val oscuro: Boolean, val noche: Boolean) {
    private val p = PaletaColonos
    /** Si arde la bombilla. */
    val lumbre: Boolean get() = oscuro || noche
    /** Los Sueños de noche: la bombilla baja y la luna por la ventana. */
    val lunar: Boolean get() = oscuro && noche
    val hondo: Color get() = if (oscuro) (if (noche) p.NOCHE_SUENOS else p.NOCHE) else SOMBRA_MUEBLE

    private fun abismo(c: Color, n: Color = hondo): Color = Color(c.red * 0.11f + n.red * 0.84f, c.green * 0.11f + n.green * 0.84f, c.blue * 0.11f + n.blue * 0.84f, c.alpha)
    fun calor(br: Float): Color = if (br > 0.02f) mezcla(p.BOMBILLA, p.BRASA, br) else p.BOMBILLA

    /** Cuánto de su propio color conserva algo de noche con [k] de luz: sube deprisa cerca de la luz y cae a casi nada lejos. */
    private fun cuanto(k: Float): Float = (1.15f * k.coerceIn(0f, 1f).pow(1.12f)).coerceAtMost(0.95f)

    /** Un color con la luz que le llega. */
    fun luz(c: Color, k: Float, br: Float = 0f, f: Float = 0f): Color = luzSobre(c, k, br, f, hondo)

    /**
     * Como [luz], pero lo que no alumbra la luz se hunde hacia [fondo] y no hacia el añil de la noche (el turquesa de dentro de la
     * alacena, que de noche es un verde botella hondo).
     */
    fun luzSobre(c: Color, k: Float, br: Float, f: Float, fondo: Color): Color {
        val kk = k.coerceIn(0f, 1f)
        if (!oscuro) {
            if (!noche) return c
            // Al anochecer: lo que no alumbra ninguna luz, hacia el violeta del cielo; lo que alumbra la bombilla, tibio; lo que
            // mira a la ventana, del naranja del poniente.
            val alumbrado = maxOf(kk, f * 0.8f)
            var v = mezcla(c, p.ANOCHECER, 0.24f - 0.16f * alumbrado)
            v = mezcla(v, p.BOMBILLA, 0.18f * kk)
            return if (f > 0.01f) mezcla(v, mezcla(v, p.PONIENTE, 0.6f), (0.55f * f * (1f - kk)).coerceIn(0f, 1f)) else v
        }
        var v = mezcla(abismo(c, fondo), c, cuanto(kk))
        v = mezcla(v, calor(br), 0.1f * kk + 0.22f * kk * kk)
        if (lunar && f > 0.01f) v = mezcla(v, mezcla(v, p.LUNA, 0.5f), (0.85f * f * (1f - kk)).coerceIn(0f, 1f))
        return v
    }
    fun sombra(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(if (noche) a * 1.15f else a)
    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la luz según lo que le llega. */
    fun brillo(a: Float, k: Float, br: Float = 0f): Color =
        if (!oscuro) luzMueble(if (noche) a * 0.6f else a) else calor(br).copy(alpha = (a * 0.95f * k).coerceIn(0f, 1f))
    /** La luz crema del día (de noche, casi nada). */
    fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.08f else if (noche) a * 0.55f else a)
    /** El roce de la luna en lo que mira a la ventana (solo en los Sueños de noche). */
    fun helado(a: Float): Color = p.LUNA.copy(alpha = if (lunar) a.coerceIn(0f, 1f) else 0f)

    /**
     * El color del ánimo: de noche es su color de día apagado por la distancia a la luz (oscuro de su mismo tono, apenas hacia
     * la noche), no un azul gris; cerca de la luz, tibio.
     */
    fun animo(t: Color, k: Float, br: Float = 0f, f: Float = 0f): Color {
        val kk = k.coerceIn(0f, 1f)
        if (!oscuro) {
            if (!noche) return t
            var v = mezcla(t, p.ANOCHECER, 0.18f - 0.1f * maxOf(kk, f))
            v = mezcla(v, p.BOMBILLA, 0.08f * kk)
            return if (f > 0.01f) mezcla(v, mezcla(v, p.PONIENTE, 0.45f), (0.45f * f * (1f - kk)).coerceIn(0f, 1f)) else v
        }
        val n = hondo
        val apagado = Color(t.red * 0.3f + n.red * 0.38f, t.green * 0.3f + n.green * 0.38f, t.blue * 0.3f + n.blue * 0.38f, t.alpha)
        var v = mezcla(apagado, t, (0.1f + 1.0f * kk).coerceAtMost(0.95f))
        v = mezcla(v, calor(br), 0.08f * kk + 0.12f * kk * kk)
        if (lunar && f > 0.01f) v = mezcla(v, mezcla(v, p.LUNA, 0.4f), (0.7f * f * (1f - kk)).coerceIn(0f, 1f))
        return v
    }
    /**
     * El papel donde va el día: de noche, teñido de la luz que le llega (ámbar cerca, azulado y hundido lejos), pero nunca tan
     * oscuro que la tinta baje de 4.5 a 1.
     */
    fun etiqueta(k: Float, base: Color = p.PAPEL, br: Float = 0f): Color {
        if (!oscuro) return if (noche) mezcla(base, p.ANOCHECER, 0.1f) else base
        val ke = k.coerceIn(0f, 1f).pow(0.7f)
        val lejos = mezcla(base, if (noche) Color(0xFF2A3463) else Color(0xFF2C3656), 0.48f)
        val cerca = mezcla(base, calor(br), 0.22f)
        return mezcla(lejos, cerca, ke)
    }
    val tinta: Color get() = if (oscuro) mezcla(p.TINTA, Color.Black, 0.2f) else p.TINTA
}

// ---------------------------------------------------------------- la luz, compartida por el mueble y los volúmenes

/**
 * Lo que llega de la bombilla a un punto ([fx], [fy] en anchos del mueble): cae con la distancia, más despacio hacia abajo, y
 * más dentro del cono que abre la pantalla (por la cornisa, el costado derecho y las repisas de arriba); la pantalla de lata
 * tapa lo de arriba.
 */
internal fun bombillaColonos(fx: Float, fy: Float, baja: Boolean): Float {
    val dx = fx - MuebleEstanteColonos.LAMP_X
    val dy0 = fy - MuebleEstanteColonos.LAMP_Y
    val dy = dy0 * (if (dy0 > 0f) 0.72f else 1.3f)
    var k = (if (baja) 1.1f else 1.6f) * exp(-(if (baja) 3.6f else 3.1f) * sqrt(dx * dx + dy * dy))
    if (dy0 > 0f) {
        val ancho = maxOf(0.06f, dy0 * 0.32f)
        val cono = exp(-(dx / ancho) * (dx / ancho))
        k *= 0.45f + 0.85f * cono
    } else if (dy0 < -0.02f) k *= 0.55f
    return k
}

/** Lo que llega de la brasa de la estufa, en [e]: cálido y cerca, y más hacia arriba que hacia los lados. */
internal fun brasaColonos(fx: Float, fy: Float, e: Offset): Float {
    val dx = fx - e.x; val dy = (fy - e.y) * 0.78f
    return 1.0f * exp(-3.4f * sqrt(dx * dx + dy * dy))
}

/** Lo que llega del candil de frasco, en [c]: una luz chica que tiñe a sus vecinos. */
internal fun candilColonos(fx: Float, fy: Float, c: Offset): Float {
    val dx = fx - c.x; val dy = fy - c.y
    return 0.55f * exp(-5.5f * sqrt(dx * dx + dy * dy))
}

/** Lo que entra de la ventana de botellas (de día, el sol; al anochecer, el poniente; en los Sueños de noche, la luna). */
internal fun rayoVentanaColonos(fx: Float, fy: Float): Float {
    val dx = fx - MuebleEstanteColonos.VENT_X; val dy = fy - MuebleEstanteColonos.VENT_Y
    return (1.15f * exp(-2.2f * sqrt(dx * dx * 0.8f + dy * dy))).coerceIn(0f, 1f)
}

/**
 * Las medidas de la cocina que comparten las piezas, en píxeles: el ancho [w] y el alto [h] del lienzo, el pie de la corona
 * [ch], el borde de dentro de los costados ([x0], [x1]), el pie de la última tabla ([fondo]), una repisa con su tabla
 * ([fila]) y cuántas repisas hay ([n]). Y la luz que llega a cada punto ([k]), la parte que viene de la brasa ([br]) y la del
 * poniente o la luna ([f]).
 */
internal class MarcoColonos(val w: Float, val h: Float, val ch: Float, val x0: Float, val x1: Float, val fondo: Float, val fila: Float,
                            val n: Int, val a: AireColonos) {
    /** La franja de pared que asoma a cada lado de la alacena. */
    val borde: Float get() = w * MuebleEstanteColonos.BORDE
    /** Donde se apoya lo de encima: la cara de arriba de la cornisa, de atrás a adelante. */
    val apoyo: Float get() = ch - w * MuebleEstanteColonos.CORNISA
    val apoyoAtras: Float get() = apoyo - w * 0.024f
    /** El cuerpo bajo: los cajones bajo la última tabla, el faldón, el rincón de la pared con el piso y donde pisan las patas. */
    val cajones: Float get() = fondo + w * 0.105f
    val faldon: Float get() = fondo + w * 0.14f
    val suelo: Float get() = fondo + w * 0.16f
    val pisan: Float get() = fondo + w * 0.19f
    /** La brasa de la estufa y el candil, en anchos. */
    val brasa: Offset get() = Offset(MuebleEstanteColonos.BRASA_X, fondo / w + MuebleEstanteColonos.BRASA_DY)
    val candil: Offset get() = Offset(MuebleEstanteColonos.CANDIL_X, (ch + (fondo - ch) * 0.5f) / w)

    fun k(p: Offset): Float = luzColonos(p.x / w, p.y / w, a, brasa, candil)
    fun br(p: Offset): Float = parteBrasaColonos(p.x / w, p.y / w, a, brasa, candil)
    fun f(p: Offset): Float = if (a.noche) rayoVentanaColonos(p.x / w, p.y / w) else 0f
    fun c(col: Color, p: Offset, mas: Float = 1f): Color { val kk = k(p) * mas; return a.luz(col, kk, br(p), f(p)) }
    /** Como [c], pero lo que no alumbra la luz se hunde hacia [fondo] (el verde botella de dentro de la alacena). */
    fun cSobre(col: Color, p: Offset, fondo: Color, mas: Float = 1f): Color = a.luzSobre(col, k(p) * mas, br(p), f(p), fondo)
    /** Hacia dónde mira la luz que más le llega a [p]: 1, desde la derecha; -1, desde la izquierda. */
    fun hacia(p: Offset): Float = haciaColonos(p.x / w, p.y / w, a, brasa, candil)
}

/** Toda la luz cálida que llega a un punto: la bombilla, la brasa y el candil (al anochecer, solo la bombilla). */
internal fun luzColonos(fx: Float, fy: Float, a: AireColonos, brasa: Offset, candil: Offset): Float {
    if (!a.lumbre) return 1f
    val b = bombillaColonos(fx, fy, a.lunar)
    if (!a.oscuro) return b.coerceIn(0.02f, 1f)
    return (b + brasaColonos(fx, fy, brasa) + candilColonos(fx, fy, candil)).coerceIn(0.02f, 1f)
}

/** Qué parte de esa luz viene de la brasa (para teñirla de naranja). */
internal fun parteBrasaColonos(fx: Float, fy: Float, a: AireColonos, brasa: Offset, candil: Offset): Float {
    if (!a.oscuro) return 0f
    val b = bombillaColonos(fx, fy, a.lunar); val e = brasaColonos(fx, fy, brasa); val c = candilColonos(fx, fy, candil)
    return (e / (b + e + c + 0.001f)).coerceIn(0f, 1f)
}

/** De qué lado viene la luz que más le llega a un punto: de día, de la ventana (izquierda); de noche, la suma de las tres. */
internal fun haciaColonos(fx: Float, fy: Float, a: AireColonos, brasa: Offset, candil: Offset): Float {
    if (!a.oscuro) return -1f
    val b = bombillaColonos(fx, fy, a.lunar); val e = brasaColonos(fx, fy, brasa); val c = candilColonos(fx, fy, candil)
    fun lado(x: Float) = if (x > fx) 1f else -1f
    return if (b * lado(MuebleEstanteColonos.LAMP_X) + e * lado(brasa.x) + c * lado(candil.x) >= 0f) 1f else -1f
}

/** Para un volumen: dónde cae en anchos del mueble ([repisa] desde arriba y su centro [centro] de 0 a 1). */
internal fun lugarVolumenColonos(repisa: Int, centro: Float): Offset {
    val m = MuebleEstanteColonos
    return Offset(m.LADO + centro * (1f - 2 * m.LADO), m.CORONA + (repisa + 0.55f) * m.FILA / m.ANCHO_NOMINAL)
}

/** Dónde arde la brasa, para un volumen que no sabe cuánto mide el estante (lo supone del ancho de un teléfono). */
internal fun brasaNominalColonos(repisas: Int): Offset {
    val m = MuebleEstanteColonos
    return Offset(m.BRASA_X, m.CORONA + repisas * m.FILA / m.ANCHO_NOMINAL + m.BRASA_DY)
}

/** Dónde arde el candil, para un volumen: a media altura del cuerpo, en el costado izquierdo. */
internal fun candilNominalColonos(repisas: Int): Offset {
    val m = MuebleEstanteColonos
    return Offset(m.CANDIL_X, m.CORONA + repisas * m.FILA / m.ANCHO_NOMINAL * 0.5f)
}

// ---------------------------------------------------------------- lo fijo pintado una vez

/**
 * La corona pintada una vez por tamaño y modo, en dos ranuras (el diario y los Sueños). Pegar una imagen cuesta mucho menos
 * que repetir la pared, la ventana y el bodegón en cada dibujo. Unos 2.8 MB por ranura en un teléfono de 1080 px.
 */
internal object CapaColonos {
    private val claves = arrayOfNulls<String>(2)
    private val imagenes = arrayOfNulls<ImageBitmap>(2)

    /**
     * Pega la imagen de [r] (la pinta antes si su clave cambió). [debajo], si va, se pinta primero, sin el recorte de [r], y la
     * imagen sigue hasta [abajo]: es lo que cubre la fila de la junta que [r] corta a medio píxel.
     */
    fun DrawScope.pegar(ranura: Int, clave: String, r: Rect, debajo: (DrawScope.() -> Unit)? = null, abajo: Float = r.bottom,
                        pintar: DrawScope.() -> Unit) {
        val ancho = r.width.roundToInt(); val alto = r.height.roundToInt()
        if (ancho < 1 || alto < 1 || ranura !in 0..1) return
        var img = imagenes[ranura]
        if (img == null || claves[ranura] != clave) {
            val base = ImageBitmap(ancho, alto)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(base), Size(ancho.toFloat(), alto.toFloat())) {
                translate(-r.left, -r.top) { clipRect(r.left, r.top, r.right, r.bottom) { pintar() } }
            }
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

/** La fila de la junta en [y], de [x0] a [x1], de [c], debajo de las dos piezas que se tocan ahí; si [y] es entero, nada. */
internal fun DrawScope.filaJuntaColonos(y: Float, x0: Float, x1: Float, c: Color) {
    val f = floor(y)
    if (f == y || x1 <= x0) return
    drawRect(c, Offset(x0, f), Size(x1 - x0, 1f))
}

// ---------------------------------------------------------------- ayudantes de forma

internal fun poligonoColonos(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

internal fun corridoColonos(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

/** La sombra de lo que pisa: un óvalo corrido hacia donde no da la luz. */
internal fun DrawScope.contactoColonos(centro: Offset, ancho: Float, alto: Float, a: AireColonos, fuerza: Float = 0.34f, haciaDer: Boolean = true) =
    drawOval(a.sombra(fuerza), Offset(centro.x - ancho * (if (haciaDer) 0.42f else 0.58f), centro.y - alto / 2), Size(ancho, alto))

/** Un clavo de cabeza redonda visto de frente: su sombra, la cabeza y el punto de luz. */
internal fun DrawScope.clavoColonos(p: Offset, r: Float, g: MarcoColonos, metal: Color = PaletaColonos.HIERRO) {
    val k = g.k(p)
    drawCircle(g.a.sombra(0.4f), r * 1.05f, p + Offset(r * 0.45f, r * 0.5f))
    drawCircle(g.a.luz(metal, k, g.br(p)), r, p)
    drawCircle(g.a.brillo(0.55f, k, g.br(p)), r * 0.38f, p - Offset(r * 0.3f, r * 0.3f))
}

/**
 * El dibujo del estante del diario de Colonos (lo usa `EstanteColonos`, en `BibliotecaFuturo.kt`): la alacena de la cocina de
 * la casa del almacén de El Remanso, en 2288. Es un trastero de cocina de antes de la guerra, rescatado de una granja en ruinas
 * y arreglado: pino pintado de amarillo maíz, gastado hasta la madera donde roza, con el interior de turquesa; la cornisa volada
 * con su cimacio, el friso de puntos y el faldón recortado en ondas; los costados de dos tablas con su media caña, las bisagras
 * de la puerta de vidrio que ya no está, un remiendo de lata y el trapo colgado; las tablas gruesas de canto redondo (una,
 * repuesta sin pintar); el fondo de tablillas machihembradas; abajo, dos cajones con perillas de madera, el faldón recortado y
 * las patas. Contra la pared de adobe encalada con añil, con la cenefa pintada, la viga con el cable de la luz y la ventana de
 * botellas. Encima, la radio de onda corta en su caja de nogal (manda), con el tapete de ganchillo, la batería, la lata de
 * girasoles, la calabaza, la cafetera de peltre, la charola pintada y las revistas atadas; en la pared, la sartén, el colador,
 * las hierbas y la bombilla del generador con su pantalla de lata. Al pie, el piso de barro con el tapete trenzado, la estufa de
 * lámina con la tetera, la leña y la cubeta con elotes. La pared y el piso están en `MuebleEstanteColonosMuro.kt`; la ventana y
 * lo que cuelga, en `MuebleEstanteColonosPared.kt`; la alacena, en `MuebleEstanteColonosAlacena.kt`; el bodegón de arriba, en
 * `MuebleEstanteColonosCorona.kt` y `MuebleEstanteColonosRadio.kt`; lo del pie, en `MuebleEstanteColonosPie.kt`; lo de los
 * huecos, en `MuebleEstanteColonosHuecos.kt`; las entradas, en `MuebleEstanteColonosVolumenes.kt`.
 */
internal object MuebleEstanteColonos {
    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteColonos` las lee al dibujar)

    val CORONA = 0.6f
    val BASE = 0.36f
    val LADO = 0.085f
    /** La franja de pared que asoma a cada lado de la alacena, en anchos. */
    val BORDE = 0.03f
    val ALTO_REPISA: Dp = 110.dp
    val TABLA: Dp = 18.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (34 + (palabras / 36).coerceAtMost(18)).dp

    /** Cuál de los tres es una entrada: 0, la caja de cereal; 1, el cuaderno de tapas de lámina; 2, el fardo de tela de costal. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla, 71)
        return if (x < 0.38f) 0 else if (x < 0.7f) 1 else 2
    }

    /** La caja, la más alta; el cuaderno, con sus anillas; el fardo, más bajo y asentado. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.87f + 0.06f * azar(semilla, 1)
        1 -> 0.83f + 0.05f * azar(semilla, 1)
        else -> 0.8f + 0.06f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** La bombilla (el centro del vidrio), en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val LAMP_X = 0.835f
    const val LAMP_Y = 0.232f
    /** El centro de la ventana de botellas. */
    const val VENT_X = 0.14f
    const val VENT_Y = 0.18f
    /** La brasa de la estufa: su x y su altura bajo la última tabla, en anchos. */
    const val BRASA_X = 0.1f
    const val BRASA_DY = 0.24f
    /** El candil de frasco: colgado a media altura del costado izquierdo. */
    const val CANDIL_X = 0.058f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble: el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 128f
    /** La cornisa, en anchos: de donde pisan las cosas al arranque de las repisas. */
    const val CORNISA = 0.108f

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireColonos(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * u
        val fondo = if (n > 0) d.tablas.last().bottom else ch
        val g = MarcoColonos(w, h, ch, d.izquierda.right, d.derecha.left, fondo, fila, n, a)

        // ---- lo fijo de arriba (la pared, la ventana, la viga, la bombilla, la cornisa y el bodegón), pintado una vez por
        // tamaño y modo. Si el borde de la corona cae a medio píxel, la imagen llega al píxel entero de abajo y, debajo de esa
        // fila, va la corona corrida un píxel: así no asoma el fondo entre la corona y el cuerpo.
        val clave = "${w.toInt()}|${ch.toInt()}|${fila.toInt()}|$n|$oscuro|$noche|$u"
        val chI = ceil(ch)
        with(CapaColonos) {
            val debajo: (DrawScope.() -> Unit)? = if (chI > ch && chI >= 2f) {
                { clipRect(0f, chI - 1f, w, chI) { translate(0f, 1f) { coronaColonos(g); granoMueble(Rect(0f, 0f, w, ch), oscuro) } } }
            } else null
            pegar(if (noche) 1 else 0, clave, Rect(0f, 0f, w, ch), debajo = debajo, abajo = chI) {
                coronaColonos(g); granoMueble(Rect(0f, 0f, w, ch), oscuro)
            }
        }
        if (fondo - ch < 1f) return

        // ---- el cuerpo: la pared de los lados, cada casillero, los costados, las tablas y los rótulos
        paredLadosColonos(g)
        // Las juntas de cada casillero con la tabla de debajo, que pueden caer a medio píxel: debajo de los dos, esa fila.
        d.tablas.forEach { t -> filaJuntaColonos(t.top, g.x0, g.x1, g.c(PaletaColonos.MAIZ, Offset(w * 0.5f, t.top))) }
        d.repisas.forEachIndexed { k, r -> casilleroColonos(k, r, d.ocupado.getOrNull(k) ?: r.left, g) }
        costadosColonos(g, d.tablas)
        d.tablas.forEachIndexed { k, r -> tablaColonos(k, r, k == d.tablas.size - 1, g) }
        d.repisas.forEachIndexed { k, r ->
            val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
            huecoLibreColonos(k, r, oc, g)
        }
        // De día, la luz de la ventana baja de arriba a la izquierda: el cuerpo se apaga un poco hacia abajo a la derecha.
        if (!oscuro) drawRect(Brush.linearGradient(if (noche) listOf(PaletaColonos.PONIENTE.copy(alpha = 0.12f), Color.Transparent, PaletaColonos.ANOCHECER.copy(alpha = 0.26f))
            else listOf(Color.Transparent, sombraMueble(0.16f)), Offset(0f, ch), Offset(w, fondo)),
            Offset(0f, ch), Size(w, fondo - ch))
        d.placas.forEach { p -> rotuloColonos(p, medidor, g) }
        if (fondo - ch > w * 0.25f) candilFrascoColonos(g)

        // ---- el pie: el cuerpo bajo, el piso y lo que hay en él
        pieColonos(g)
        granoMueble(Rect(0f, ch, w, h), oscuro)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) = volumenColonos(v, medidor, oscuro)
}
