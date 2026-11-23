package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
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
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * La paleta del cuarto de guardar de K'an (una ciudad maya de las tierras bajas, hacia el 750), de la misma casa que el
 * Ideario: el azul maya del testero y su sombra honda, el estuco crema, el cinabrio de las bandas, la caliza de la bóveda y
 * de las zapatas; el chicozapote del anaquel y el henequén de sus amarres; lo guardado (el amate, la piel de venado, el
 * algodón, el jade) y lo de arriba (el cacao, el maíz, el ocote, el carey). La sombra es violeta (`sombraMueble`); la luz, crema.
 */
internal object MesoTinta {
    /** El azul maya del testero: su luz (al sol), su tono, su repaso y el azul honda del fondo de los huecos. */
    val AZUL_LUZ = Color(0xFF7FC0C6)
    val AZUL = Color(0xFF3F8FA2)
    val AZUL_REPASO = Color(0xFF4E9AA8)
    val AZUL_HONDO = Color(0xFF1D4652)
    /** El estuco crema y su sombra; la caliza que asoma y la de las lajas de la bóveda y el piso. */
    val ESTUCO = Color(0xFFF2E8D2)
    val ESTUCO_OSC = Color(0xFFCDBE9E)
    val CALIZA = Color(0xFFC9BC9E)
    val CALIZA_OSC = Color(0xFF8E846E)
    /** El cinabrio de las bandas y su hondo. */
    val CINABRIO = Color(0xFFA9341F)
    val CINABRIO_OSC = Color(0xFF6E2014)
    /** El chicozapote del anaquel: su luz, su tono y su sombra. */
    val ZAPOTE_LUZ = Color(0xFFB8714E)
    val ZAPOTE = Color(0xFF8C4A33)
    val ZAPOTE_OSC = Color(0xFF4F2619)
    /** El henequén de los amarres, crudo y su sombra. */
    val HENEQUEN = Color(0xFFD7C08C)
    val HENEQUEN_OSC = Color(0xFF9C8553)
    /** El amate (la hoja aclarada) y su fibra; el negro de humo (también la tinta). */
    val AMATE = Color(0xFFE3D0A8)
    val AMATE_OSC = Color(0xFFA88C62)
    val TINTA = Color(0xFF2A1810)
    /** El jade y su luz; la piel de venado curtida y el algodón crudo. */
    val JADE = Color(0xFF2F8A5E)
    val JADE_LUZ = Color(0xFF8FD3A8)
    val VENADO = Color(0xFFBF8A57)
    val ALGODON = Color(0xFFEFE6D2)
    /** El añil y el achiote de los tintes. */
    val ANIL = Color(0xFF2E4F8E)
    val ACHIOTE = Color(0xFFD7642A)
    /** El cacao: la mazorca madura (amarilla, roja y granate), la pulpa y la semilla. */
    val CACAO_AMARILLO = Color(0xFFDDA23A)
    val CACAO_ROJO = Color(0xFFC04A26)
    val CACAO_GRANATE = Color(0xFF7E2A22)
    val PULPA = Color(0xFFF4EEDC)
    val SEMILLA = Color(0xFF5E3424)
    /** El maíz (los granos y las hojas secas) y el bejuco del canasto. */
    val MAIZ = Color(0xFFE8B443)
    val MAIZ_ROJO = Color(0xFFB2452E)
    val TOTOMOXTLE = Color(0xFFDCC48F)
    val BEJUCO = Color(0xFFC39A5C)
    val BEJUCO_OSC = Color(0xFF7F5B30)
    /** El ocote (pino resinoso), el carey de la tortuga y el barro cocido. */
    val OCOTE = Color(0xFFC98B4C)
    val CAREY = Color(0xFF8A5A2C)
    val CAREY_LUZ = Color(0xFFD6A15A)
    val BARRO = Color(0xFFB0673C)
    val BARRO_OSC = Color(0xFF6E3B22)
    val CENIZA = Color(0xFFB9B0A6)

    /**
     * Los colores del ánimo, los del mundo, de bajo a alto: cinabrio, ocre de achiote, amate (el neutro), turquesa y azul maya.
     * Con cinco paradas el paso del amate al azul no se vuelve gris.
     */
    val ANIMO = listOf(Color(0xFFA9341F), Color(0xFFCB7A34), Color(0xFFE2CFA4), Color(0xFF55A8A6), Color(0xFF2A73A0))

    // Las luces: la noche del cuarto (azul violácea), su sombra, la de los Sueños, el atardecer, la llama y la luna.
    val NOCHE = Color(0xFF211F38)
    val NOCHE_SOMBRA = Color(0xFF100E1D)
    val NOCHE_SUENOS = Color(0xFF1C2140)
    val ATARDECER = Color(0xFF6C5A8C)
    val OCASO = Color(0xFFFF9A5A)
    val SOL = Color(0xFFFFF3DA)
    val LLAMA = Color(0xFFFFC46A)
    val LLAMA_VIVA = Color(0xFFFFE6A8)
    val BRASA = Color(0xFFFF8A3C)
    val LUNA = Color(0xFFA8C2F0)
}

/**
 * El aire del momento en el cuarto de guardar. De día, el sol que entra por la puerta (a la izquierda, fuera del cuadro) y
 * deja su mancha recortada en el testero; de noche (el modo oscuro) arden dos lumbres, la del nicho (arriba a la derecha) y la
 * del fogón (abajo a la izquierda, en primer plano): cada cosa va de su color hondo (de su mismo tono, hacia el azul violáceo
 * del cuarto) a su color según lo que le llega de las dos, y la luna, que entra por la misma puerta, deja una mancha fría y
 * chica arriba a la izquierda. En los Sueños ([noche]) en claro, el atardecer: la mancha de la puerta, baja y anaranjada, y el
 * resto en sombra lila; en oscuro ([lunar]), las dos lumbres arden bajas y manda la luna.
 * [k] es cuánto llega de las lumbres (de 0 a 1; de día, 1) y [f], de la puerta (el sol, el ocaso o la luna).
 */
internal class AireMeso(val oscuro: Boolean, val noche: Boolean) {
    val lumbre: Boolean get() = oscuro || noche
    val lunar: Boolean get() = oscuro && noche
    val ocaso: Boolean get() = noche && !oscuro
    private val noct: Color get() = if (lunar) MesoTinta.NOCHE_SUENOS else MesoTinta.NOCHE

    /** El color propio de algo, hundido en la noche: oscuro y de su mismo tono, hacia el azul violáceo del cuarto. */
    private fun hondo(c: Color) = Color(c.red * 0.2f + noct.red * 0.62f, c.green * 0.2f + noct.green * 0.62f, c.blue * 0.2f + noct.blue * 0.62f, c.alpha)

    /** Lo que toca la luna: el color de la cosa, apagado y enfriado (con su tono, no un gris). */
    private fun lunado(c: Color) = Color(c.red * 0.42f + 0.06f, c.green * 0.48f + 0.09f, c.blue * 0.55f + 0.22f, c.alpha)

    /** El color de algo con la luz que le llega: [k] de las lumbres, [f] de la puerta. */
    fun c(col: Color, k: Float, f: Float = 0f): Color {
        if (!oscuro) {
            if (!noche) return col
            return mezcla(mezcla(col, MesoTinta.ATARDECER, 0.32f * (1f - f)), MesoTinta.OCASO, 0.2f * f + 0.08f * k)
        }
        val kk = k.coerceIn(0f, 1f)
        var v = mezcla(hondo(col), col, (1.02f * kk).coerceAtMost(0.92f))
        // Cerca de la lumbre todo se calienta de verdad (también el azul), y lejos queda en su hondo.
        v = mezcla(v, MesoTinta.LLAMA, 0.18f * kk + 0.36f * kk * kk)
        val ff = f.coerceIn(0f, 1f) * (1f - kk) * (if (lunar) 0.7f else 0.36f)
        return if (ff > 0f) mezcla(v, lunado(col), ff) else v
    }
    /** La sombra: de día violeta; de noche, la noche del cuarto. */
    fun s(a: Float): Color = when {
        oscuro -> MesoTinta.NOCHE_SOMBRA.copy(alpha = (a * 1.3f).coerceIn(0f, 1f))
        noche -> sombraMueble((a * 1.2f).coerceIn(0f, 1f))
        else -> sombraMueble(a)
    }
    /** El brillo de lo que mira a la luz: de día crema; de noche, el filo de la lumbre según [k]. */
    fun b(a: Float, k: Float = 1f): Color = when {
        oscuro -> MesoTinta.LLAMA.copy(alpha = (a * 0.95f * k).coerceIn(0f, 1f))
        noche -> luzMueble(a * 0.65f)
        else -> luzMueble(a)
    }
    /** El filo frío de la luna en lo que mira a la puerta (de noche). */
    fun helado(a: Float, f: Float): Color = MesoTinta.LUNA.copy(alpha = if (oscuro) (a * f).coerceIn(0f, 1f) else 0f)
    /** La luz de la puerta sobre lo que toca: el sol (de día), el ocaso (Sueños en claro) o la luna (de noche). */
    fun puerta(a: Float): Color = when {
        !lumbre -> MesoTinta.SOL.copy(alpha = a.coerceIn(0f, 1f))
        !oscuro -> MesoTinta.OCASO.copy(alpha = (a * 0.8f).coerceIn(0f, 1f))
        else -> MesoTinta.LUNA.copy(alpha = (a * (if (lunar) 0.42f else 0.26f)).coerceIn(0f, 1f))
    }
    /** El color del ánimo: de noche se apaga con la distancia a las lumbres, sin perder su tinte. */
    fun animo(t: Color, k: Float, f: Float = 0f): Color {
        if (!oscuro) return c(t, k, f)
        val kk = k.coerceIn(0f, 1f)
        var v = mezcla(Color(t.red * 0.46f + noct.red * 0.4f, t.green * 0.46f + noct.green * 0.4f, t.blue * 0.46f + noct.blue * 0.4f), t, (1.08f * kk).coerceAtMost(0.96f))
        v = mezcla(v, MesoTinta.LLAMA, 0.16f * kk)
        val ff = f.coerceIn(0f, 1f) * (1f - kk) * (if (lunar) 0.45f else 0.3f)
        return if (ff > 0f) mezcla(v, lunado(t), ff) else v
    }
    /** El estuco o el amate donde va el día: casi no se apaga, para que el número se lea siempre (4.5 a 1 o más). */
    fun etiqueta(k: Float, base: Color = MesoTinta.ESTUCO): Color {
        val kk = k.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(base, MesoTinta.ATARDECER, 0.12f) else base
        // Lejos de la lumbre, el estuco se apaga hasta un gris violáceo medio (con la tinta, unos 6 a 1); cerca, se calienta.
        val lejos = mezcla(base, Color(0xFF6E6678), if (lunar) 0.52f else 0.46f)
        return mezcla(lejos, mezcla(base, MesoTinta.LLAMA, 0.18f), (1.3f * kk).coerceAtMost(1f))
    }
    val tinta: Color get() = MesoTinta.TINTA
    val rubrica: Color get() = if (oscuro) mezcla(MesoTinta.CINABRIO, MesoTinta.TINTA, 0.2f) else MesoTinta.CINABRIO
}

/**
 * El dibujo del estante del diario de Mesoamérica (lo usa `EstanteMeso`, en `BibliotecaAntiguedad.kt`): el cuarto de guardar
 * de la casa de K'an, bajo la bóveda falsa, con el testero de azul maya, la banda de cinabrio, la viga de amarre y el nicho de
 * la lumbre; contra él, el anaquel de chicozapote labrado a hacha de piedra, con su remate de moldura escalonada, las tablas
 * acuñadas en los postes, los amarres de henequén y las zapatas de caliza. Arriba, el canasto de cacao con las mantas, el
 * carey, el copal y el ocote; colgadas, las mazorcas y las madejas. Al pie, el fogón de tres piedras y el metate sobre las
 * lajas. El muro, la bóveda y el piso van en `MuebleEstanteMesoMuro.kt`; el anaquel, en `MuebleEstanteMesoAnaquel.kt`; lo de
 * arriba, en `MuebleEstanteMesoPiezas.kt`; el pie y las lumbres, en `MuebleEstanteMesoPie.kt`; lo de los huecos, en
 * `MuebleEstanteMesoHuecos.kt`; los volúmenes, en `MuebleEstanteMesoVolumenes.kt`.
 */
internal object MuebleEstanteMeso {
    val LETRA: FontFamily = LetraExtra.codice

    // ---------------------------------------------------------------- las medidas

    val CORONA = 0.62f
    val BASE = 0.34f
    val LADO = 0.098f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 18.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (25 + (palabras / 30).coerceAtMost(19)).dp

    /** Cuál de los tres es una entrada: 0, el códice cerrado; 1, la bolsa de piel de venado; 2, el rollo de manta. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla, 97)
        return if (x < 0.4f) 0 else if (x < 0.7f) 1 else 2
    }

    /** El códice, el más alto; el rollo, poco menos; la bolsa, la más baja. Todos llenan casi el hueco (del 80 al 95 %). */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.88f + 0.07f * azar(semilla, 1)
        1 -> 0.8f + 0.06f * azar(semilla, 1)
        else -> 0.84f + 0.07f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz (en anchos del mueble)

    /** La lumbre del nicho: su centro desde la esquina de arriba a la izquierda. */
    const val NICHO_X = 0.805f
    const val NICHO_Y = 0.405f
    /** El fogón: [FOGON_X] desde la izquierda y [FOGON_BAJO] por debajo del borde de arriba de la base. */
    const val FOGON_X = 0.15f
    const val FOGON_BAJO = 0.19f
    /** La mancha de la luna (de noche), en anchos desde la esquina de arriba a la izquierda. */
    const val LUNA_X = 0.16f
    const val LUNA_Y = 0.24f
    /** El ancho del lienzo del estante en dp en un teléfono común (para ubicar los volúmenes, que no lo saben). */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 130f
    /** Dónde pisan las cosas de arriba (la cara del remate) y dónde arranca la bóveda, en anchos. */
    const val APOYO = 0.505f
    const val ARRANQUE = 0.25f
    /** En la base: dónde empieza el piso, en anchos desde su borde de arriba. */
    const val PISO = 0.085f

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireMeso(oscuro, noche)
        val ch = d.corona.bottom
        val b = d.base
        val fondo = d.tablas.lastOrNull()?.bottom ?: ch
        val m = MesaMeso(a, w, ch, b.top, h)
        val clave = "$w|$ch|${b.top}|$fondo|$h|$density|$oscuro|$noche"
        val ranura = if (noche) 2 else 0
        // ---- arriba (pintado una vez por tamaño y modo): el testero, la bóveda, la viga, lo colgado, el remate y el bodegón
        val corona: DrawScope.() -> Unit = {
            testeroMeso(m)
            bovedaMeso(m)
            nichoMeso(m)
            colgadoMeso(m)
            remateMeso(m)
            bodegonMeso(m)
            if (a.lumbre) lumbreNichoMeso(m)
            granoMueble(Rect(0f, 0f, w, ch), oscuro)
        }
        // Si el borde de la corona (ch) cae a medio píxel, la imagen llega hasta el píxel entero de abajo (chI) y, debajo de
        // esa fila, va la corona corrida un píxel: el fondo no asoma en la junta con el cuerpo.
        val chI = ceil(ch)
        val debajo: (DrawScope.() -> Unit)? = if (chI > ch && chI >= 2f) {
            { clipRect(0f, chI - 1f, w, chI) { translate(0f, 1f) { corona() } } }
        } else null
        with(CapaMeso) { pegar(ranura, clave, Rect(0f, 0f, w, ch), debajo = debajo, abajo = chI, pintar = corona) }
        // ---- el cuerpo (cambia con lo guardado): el fondo de cada hueco, los postes, las tablas y lo de los huecos libres
        clipRect(0f, ch, w, fondo) { fondosHuecosMeso(m, d.repisas) }
        if (d.tablas.isNotEmpty()) filaJuntaMeso(m, fondo, 0f, w)
        tablasMeso(m, d)
        clipRect(0f, ch, w, fondo) { postesMeso(m, ch, fondo) }
        amarresMeso(m, d)
        d.placas.forEach { p -> rotuloMeso(m, p, medidor) }
        d.repisas.forEachIndexed { i, r -> d.ocupado.getOrNull(i)?.let { oc -> huecoLibreMeso(m, i, r, oc) } }
        granoMueble(Rect(0f, ch, w, fondo), oscuro)
        // ---- abajo (pintado una vez): el zócalo, las zapatas, el piso de lajas, el fogón y el metate
        with(CapaMeso) {
            pegar(ranura + 1, clave, Rect(0f, fondo, w, h)) {
                pisoMeso(m, fondo)
                patasMeso(m, fondo)
                pieMeso(m)
                if (a.lumbre) lumbreFogonMeso(m)
                granoMueble(Rect(0f, fondo, w, h), oscuro)
            }
        }
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) = volumenMeso(v, medidor, oscuro)
}

/**
 * Lo que comparten las piezas del estante en un dibujo: el aire, el ancho, dónde acaba la corona, dónde empieza la base y el
 * alto; ya calculados, las dos lumbres, el piso, los postes y la forma de la mancha de la puerta.
 */
internal class MesaMeso(val a: AireMeso, val w: Float, val ch: Float, val base: Float, val alto: Float) {
    /** La lumbre del nicho y la del fogón, en píxeles. */
    val nicho = Offset(w * MuebleEstanteMeso.NICHO_X, w * MuebleEstanteMeso.NICHO_Y)
    val fogon = Offset(w * MuebleEstanteMeso.FOGON_X, base + w * MuebleEstanteMeso.FOGON_BAJO)
    /** Donde empieza el piso (la junta del zócalo con las lajas). */
    val piso: Float = base + w * MuebleEstanteMeso.PISO
    /** Los postes: el de la izquierda y el de la derecha (de bajo el remate a las zapatas). */
    val posteI = Rect(w * (MuebleEstanteMeso.LADO - 0.07f), ch - 1f, w * MuebleEstanteMeso.LADO, piso - w * 0.03f)
    val posteD = Rect(w * (1f - MuebleEstanteMeso.LADO), ch - 1f, w * (1.07f - MuebleEstanteMeso.LADO), piso - w * 0.03f)
    /** Dónde pisa lo de arriba. */
    val apoyo: Float = w * MuebleEstanteMeso.APOYO
    /** La mancha de la puerta en el testero, en píxeles (de día el sol; en los Sueños claros, el ocaso). */
    val puerta: Path = formaPuertaMeso(w, a.ocaso)
    /** La parte de la mancha de la puerta que cae en [r] (un camino ya cortado: pintarlo es más barato que recortar). */
    fun puertaEn(r: Rect): Path = Path().apply { op(puerta, Path().apply { addRect(r) }, PathOperation.Intersect) }

    /** Lo que llega del nicho y del fogón a un punto, cada una por separado. */
    fun k1(p: Offset): Float = nichoLuzMeso((p.x - nicho.x) / w, (p.y - nicho.y) / w, a.lunar)
    fun k2(p: Offset): Float = fogonLuzMeso((p.x - fogon.x) / w, (p.y - fogon.y) / w, a.lunar)
    /** Cuánto llega de las dos lumbres a un punto (de día, todo). */
    fun k(p: Offset): Float = if (a.lumbre) juntarLucesMeso(k1(p), k2(p)) else 1f
    /** Cuánto llega de la puerta (el sol de día, el ocaso en los Sueños claros, la luna de noche). */
    fun f(p: Offset): Float = if (a.oscuro) lunaMeso(p.x / w, p.y / w, a.lunar) else luzPuertaMeso(p.x / w, p.y / w, a.ocaso)
    /** De noche, si la luz le llega a algo en [x], [y] por la derecha (de la lumbre que más le alumbra). */
    fun der(x: Float, y: Float): Boolean {
        if (!a.oscuro) return false
        val p = Offset(x, y)
        val lx = if (k1(p) > k2(p)) nicho.x else fogon.x
        return x < lx - w * 0.02f
    }
    /** La lumbre que más alumbra un punto (para las sombras de noche). */
    fun lumbreDe(p: Offset): Offset = if (k1(p) > k2(p)) nicho else fogon
}

// ---------------------------------------------------------------- la luz, compartida por el mueble y los volúmenes

/** Una luz que cae con la distancia [d] (en anchos) y se acaba del todo a su [alcance]. */
private fun caidaMeso(d: Float, a: Float, e: Float, alcance: Float): Float {
    val fin = a * exp(-e * alcance)
    return ((a * exp(-e * d) - fin) / (1f - fin)).coerceIn(0f, 1f)
}

/** Cuánta luz de la lumbre del nicho llega a un punto a [dx] y [dy] anchos de ella. Con [baja] (los Sueños de noche), menos. */
internal fun nichoLuzMeso(dx: Float, dy: Float, baja: Boolean = false): Float {
    val d = sqrt(dx * dx + dy * dy)
    return if (baja) caidaMeso(d, 1.0f, 4.6f, 0.55f) else caidaMeso(d, 1.5f, 2.9f, 0.95f)
}

/** Lo mismo para el fogón, que arde más grande. */
internal fun fogonLuzMeso(dx: Float, dy: Float, baja: Boolean = false): Float {
    val d = sqrt(dx * dx + dy * dy)
    return if (baja) caidaMeso(d, 1.0f, 4.2f, 0.7f) else caidaMeso(d, 1.6f, 2.6f, 1.1f)
}

/** El alcance de cada lumbre, en anchos (donde su luz ya es cero). */
internal fun alcanceLumbreMeso(nicho: Boolean, baja: Boolean): Float = if (nicho) (if (baja) 0.55f else 0.95f) else (if (baja) 0.7f else 1.1f)

/** Dos luces juntas: lo que no deja pasar ninguna de las dos. */
internal fun juntarLucesMeso(k1: Float, k2: Float): Float = 1f - (1f - k1) * (1f - k2)

/** La luna (de noche): una mancha chica de borde blando arriba a la izquierda, donde de día da el sol; grande en los Sueños. */
internal fun lunaMeso(x: Float, y: Float, lunar: Boolean): Float {
    val dx = x - (if (lunar) MuebleEstanteMeso.LUNA_X + 0.06f else MuebleEstanteMeso.LUNA_X)
    val dy = y - (if (lunar) MuebleEstanteMeso.LUNA_Y + 0.16f else MuebleEstanteMeso.LUNA_Y)
    val r = if (lunar) 0.85f else 0.42f
    return ((1f - sqrt(dx * dx + dy * dy) / r) * (if (lunar) 1f else 0.85f)).coerceIn(0f, 1f)
}

/**
 * La mancha de la puerta en el testero, en anchos desde la esquina de arriba a la izquierda: un cuadrilátero con el borde de
 * arriba (el dintel, con la cortina recogida) y el de la derecha (la jamba) en diagonal, que sigue por el anaquel. Con [ocaso]
 * (los Sueños en claro), el sol ya bajo: más larga y más baja.
 */
private fun puntosPuertaMeso(ocaso: Boolean): FloatArray =
    if (ocaso) floatArrayOf(0f, 0.3f, 0.62f, 0.4f, 1.05f, 1.45f, 0f, 1.3f)
    else floatArrayOf(0f, 0.232f, 0.46f, 0.3f, 0.7f, 1.02f, 0f, 1.08f)

internal fun formaPuertaMeso(w: Float, ocaso: Boolean): Path {
    val q = puntosPuertaMeso(ocaso)
    // El borde de arriba, ondulado por la cortina de manta recogida sobre el dintel.
    return Path().apply {
        moveTo(-2f, q[1] * w)
        val n = 6
        for (j in 1..n) {
            val t = j / n.toFloat()
            val x = q[2] * w * t
            val y = (q[1] + (q[3] - q[1]) * t) * w
            val xm = q[2] * w * (t - 0.5f / n)
            val ym = (q[1] + (q[3] - q[1]) * (t - 0.5f / n)) * w + w * 0.014f
            quadraticTo(xm, ym, x, y)
        }
        lineTo(q[4] * w, q[5] * w)
        lineTo(-2f, q[7] * w)
        close()
    }
}

/** Cuánto da la luz de la puerta (de 0 a 1) en un punto a [x] e [y] anchos de la esquina: 1 dentro, con el borde un poco suave. */
internal fun luzPuertaMeso(x: Float, y: Float, ocaso: Boolean): Float {
    val q = puntosPuertaMeso(ocaso)
    if (x < 0f) return 0f
    val arriba = q[1] + (q[3] - q[1]) * x / q[2]
    val abajo = q[7] + (q[5] - q[7]) * x / q[4]
    val derecha = q[2] + (q[4] - q[2]) * ((y - q[3]) / (q[5] - q[3])).coerceIn(0f, 1f)
    return minOf((y - arriba) / 0.025f, (abajo - y) / 0.035f, (derecha - x) / 0.03f).coerceIn(0f, 1f)
}

/** La luz de las lumbres, de la puerta y de qué lado le llega la de las lumbres a un volumen. */
internal class LuzEnVolumenMeso(val k: Float, val f: Float, val der: Boolean)

internal fun luzVolumenMeso(v: Volumen, oscuro: Boolean): LuzEnVolumenMeso {
    val m = MuebleEstanteMeso
    val fx = m.LADO + v.centro * (1f - 2 * m.LADO)
    val y = m.CORONA + (v.repisa + 0.55f) * m.FILA / m.ANCHO_NOMINAL
    if (!oscuro && !v.noche) return LuzEnVolumenMeso(1f, luzPuertaMeso(fx, y, false), false)
    val baja = oscuro && v.noche
    val base = m.CORONA + v.repisas * m.FILA / m.ANCHO_NOMINAL
    val k1 = nichoLuzMeso(fx - m.NICHO_X, y - m.NICHO_Y, baja)
    val k2 = fogonLuzMeso(fx - m.FOGON_X, y - (base + m.FOGON_BAJO), baja)
    val k = juntarLucesMeso(k1, k2)
    val f = if (oscuro) lunaMeso(fx, y, baja) else luzPuertaMeso(fx, y, true)
    val lx = if (k1 > k2) m.NICHO_X else m.FOGON_X
    return LuzEnVolumenMeso(k, f, oscuro && fx < lx - 0.02f)
}

/** Las escalas del ánimo de Mesoamérica, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_MESO: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.MESO, false) to juegoTonos(TemaId.MESO, true) }

private fun cuadradoMeso(x: Float) = x * x

/**
 * Dónde cae [t] en la escala [escala] (de bajo a alto, de 0 a 1, el neutro en 0.5): el tramo y la fracción que más se le
 * acercan; -1 si no está en la escala.
 */
private fun enEscalaMeso(t: Color, escala: List<Color>): Float {
    if (escala.size < 3) return -1f
    var mejor = -1f
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoMeso(t.red - a0.red - dr * f) + cuadradoMeso(t.green - a0.green - dg * f) + cuadradoMeso(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * El color del ánimo tal como es de día. En el modo oscuro la escala del tema cambia de colores (el neutro pasa del amate a un
 * casi negro, pensado para la interfaz) y un volumen claro se volvía negro de noche. Aquí el volumen es un objeto del cuarto:
 * conserva su color de día y la luz lo apaga con la distancia a las lumbres. Se ubica el color en la escala de noche y se toma
 * el mismo punto en la de día; si no está en la escala, queda igual.
 */
internal fun tonoDeDiaMeso(t: Color, oscuro: Boolean): Color {
    if (!oscuro) return t
    val (dia, noche) = TONOS_MESO
    val x = enEscalaMeso(t, noche)
    if (x < 0f || dia.size < 3) return t
    val s = x * 2f
    val i = s.toInt().coerceIn(0, 1)
    return mezcla(dia[i], dia[i + 1], s - i)
}

/**
 * El ánimo con los colores del mundo, con cuerpo: se ubica el color (ya de día) en la escala del tema (de 0 a 1, el neutro en
 * 0.5) y se toma el mismo punto en [MesoTinta.ANIMO]. Lo que se aparta poco del neutro se aparta un poco más (una curva que
 * conserva el orden y el neutro), para que un ánimo templado no quede en un pastel lavado. Si no está en la escala, queda igual.
 */
internal fun animoMundoMeso(t: Color): Color {
    val x0 = enEscalaMeso(t, TONOS_MESO.first)
    if (x0 < 0f) return t
    val d = (x0 - 0.5f) * 2f
    val x = 0.5f + 0.5f * kotlin.math.sign(d) * Math.pow(kotlin.math.abs(d).toDouble(), 0.62).toFloat()
    val e = MesoTinta.ANIMO
    val s = x * (e.size - 1)
    val i = s.toInt().coerceIn(0, e.size - 2)
    return mezcla(e[i], e[i + 1], s - i)
}

// ---------------------------------------------------------------- lo fijo pintado una vez

/**
 * Lo fijo pintado una vez: cuatro imágenes (la franja de arriba y la de abajo, del diario y de los Sueños), cada una con la
 * clave de su tamaño y su modo; si la clave cambia, se vuelve a pintar. Pegar una imagen cuesta mucho menos que repetir el
 * muro, la bóveda y el bodegón en cada dibujo. Las franjas no crecen con las repisas.
 */
internal object CapaMeso {
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

/**
 * La fila de la junta en [y] (de [x0] a [x1]), del chicozapote oscuro, debajo de las dos piezas que se tocan ahí: si [y] cae a
 * medio píxel, ninguna de las dos cubre del todo esa fila y asomaba el fondo (una línea a lo ancho).
 */
internal fun DrawScope.filaJuntaMeso(m: MesaMeso, y: Float, x0: Float, x1: Float) {
    val f = floor(y)
    if (f == y) return
    drawRect(m.a.c(MesoTinta.ZAPOTE_OSC, m.k(Offset((x0 + x1) / 2, y))), Offset(x0, f), Size(x1 - x0, 1f))
}

// ---------------------------------------------------------------- ayudantes de forma y de luz

internal fun poligonoMeso(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

internal fun corridoMeso(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

/** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
internal fun bordeMeso(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corridoMeso(p, dx, dy), PathOperation.Difference) }

/**
 * El bulto de un cuerpo redondo dentro de [r] (se llama dentro de su recorte), como en el Ideario: el plano de luz, el brillo,
 * el plano de sombra y la sombra honda del otro lado. Con [der], la luz le llega por la derecha (de noche, de la lumbre).
 */
internal fun DrawScope.cilindroMeso(r: Rect, a: AireMeso, k: Float, fuerza: Float = 1f, der: Boolean = false, luna: Float = 0f) {
    fun x(f: Float, ancho: Float) = if (der) r.right - r.width * (f + ancho) else r.left + r.width * f
    drawRect(a.b(0.2f * fuerza, k), Offset(x(0f, 0.3f), r.top), Size(r.width * 0.3f, r.height))
    drawRect(a.b(0.32f * fuerza, k), Offset(x(0.12f, 0.08f), r.top), Size(r.width * 0.08f, r.height))
    drawRect(a.s(0.2f * fuerza), Offset(x(0.6f, 0.4f), r.top), Size(r.width * 0.4f, r.height))
    drawRect(a.s(0.16f * fuerza), Offset(x(0.86f, 0.14f), r.top), Size(r.width * 0.14f, r.height))
    if (a.oscuro && luna > 0.08f) {
        drawRect(a.helado(0.2f, luna), Offset(r.left, r.top), Size(r.width * 0.3f, r.height))
        drawRect(a.helado(0.34f, luna), Offset(r.left + r.width * 0.06f, r.top), Size(r.width * 0.08f, r.height))
    }
}

/** El filo cálido de la lumbre en el canto de arriba y en el costado que la mira, de noche; de día no pinta nada. */
internal fun DrawScope.filoMeso(r: Rect, a: AireMeso, k: Float, der: Boolean) {
    if (!a.oscuro || k < 0.06f) return
    val u = density
    drawRect(MesoTinta.LLAMA.copy(alpha = (0.85f * k).coerceAtMost(0.75f)), r.topLeft, Size(r.width, 1.4f * u))
    val x = if (der) r.right - 1.3f * u else r.left
    drawRect(MesoTinta.LLAMA.copy(alpha = (0.6f * k).coerceAtMost(0.55f)), Offset(x, r.top), Size(1.3f * u, r.height))
}

/** Un brillo puntual: de día crema; de noche, el de la lumbre si llega. */
internal fun DrawScope.destelloMeso(c: Offset, r: Float, a: AireMeso, k: Float) {
    if (a.oscuro) {
        if (k > 0.08f) { drawCircle(MesoTinta.LLAMA.copy(alpha = (0.3f * k).coerceAtMost(0.28f)), r * 2.2f, c); drawCircle(Color(0xFFFFF0CF).copy(alpha = (1.1f * k).coerceAtMost(0.9f)), r, c) }
    } else drawCircle(luzMueble(if (a.noche) 0.6f else 0.85f), r, c)
}

/** La sombra de contacto de lo que pisa algo, corrida a la derecha. */
internal fun DrawScope.contactoMeso(c: Offset, ancho: Float, alto: Float, a: AireMeso, fuerza: Float = 0.4f) =
    drawOval(a.s(fuerza), Offset(c.x - ancho * 0.42f, c.y - alto / 2), Size(ancho, alto))

/**
 * La sombra que algo echa en el muro de detrás: de día, corrida a la derecha y abajo (más honda dentro de la mancha del sol);
 * de noche, hacia fuera de la lumbre que más le alumbra. [desde] es el centro de la cosa.
 */
internal fun DrawScope.sombraMuroMeso(m: MesaMeso, p: Path, desde: Offset, fuerza: Float = 0.2f) {
    val w = m.w
    val a = m.a
    if (!a.oscuro) {
        val sol = m.f(desde)
        translate(w * 0.024f, w * 0.014f) { drawPath(p, a.s(fuerza * (1f + 0.7f * sol))) }
    } else {
        val k = m.k(desde)
        if (k > 0.1f) {
            val ll = m.lumbreDe(desde)
            val dx = ((desde.x - ll.x) / w).coerceIn(-0.3f, 0.3f) / 0.3f
            val dy = if (ll.y > desde.y) -w * 0.016f else w * 0.012f
            translate(w * 0.045f * dx, dy) { drawPath(p, MesoTinta.NOCHE_SOMBRA.copy(alpha = (0.95f * k).coerceAtMost(0.55f))) }
        }
    }
}

/** Un número de 0 a 1 con la semilla del tema, para que las piezas de arriba no repitan las de otros. */
internal fun azarMeso(semilla: Int, k: Int): Float = azar(semilla * 31 + 1707, k)
