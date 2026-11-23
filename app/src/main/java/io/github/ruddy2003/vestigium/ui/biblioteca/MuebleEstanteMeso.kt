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
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

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

    // Las luces: la noche del cuarto (añil violáceo, nunca gris), su sombra, la de los Sueños, el atardecer, la llama y la luna.
    val NOCHE = Color(0xFF231C5A)
    val NOCHE_SOMBRA = Color(0xFF0E0B22)
    val NOCHE_SUENOS = Color(0xFF1C2A5E)
    /** El fondo de los huecos de noche: el azul del testero en la sombra hondísima, hacia el añil violeta. */
    val ANIL_HONDO = Color(0xFF262468)
    val ATARDECER = Color(0xFF6C5A8C)
    val OCASO = Color(0xFFFF9A5A)
    val SOL = Color(0xFFFFF3DA)
    /** El sol sobre el azul maya: lo entibia hacia un turquesa claro y cálido. */
    val SOL_AZUL = Color(0xFFA3D0B0)
    val LLAMA = Color(0xFFFFC46A)
    val LLAMA_VIVA = Color(0xFFFFE6A8)
    val BRASA = Color(0xFFFF8A3C)
    /** La brasa del fogón, más roja que la llama del nicho: abajo la noche es ámbar rojizo. */
    val BRASA_ROJA = Color(0xFFF2643A)
    val LUNA = Color(0xFFB4CCF2)
    /** El plano de luna de los Sueños: plata fría. */
    val PLATA = Color(0xFFC9D8F0)
}

/**
 * El aire del momento en el cuarto de guardar. De día, el sol que entra por la puerta (a la izquierda, fuera del cuadro) y
 * deja en el testero la forma de la puerta, recortada; de noche (el modo oscuro) arden dos lumbres, la del nicho (arriba a la
 * derecha) y la del fogón (abajo a la izquierda, en primer plano): cada cosa va de su color hondo (de su mismo tono, oscurecido
 * y apenas llevado al añil del cuarto, nunca gris) a su color según lo que le llega de las dos, y cerca de cada una se
 * calienta (ámbar arriba, ámbar rojizo abajo); la luna, que entra por la misma puerta, deja una mancha fría y chica arriba a la
 * izquierda. En los Sueños ([noche]) en claro, el atardecer: la mancha de la puerta, baja y anaranjada, y el resto en sombra
 * lila; en oscuro ([lunar]), las dos lumbres arden bajas y manda la luna: un plano de plata que entra por la puerta y cruza el
 * mueble en diagonal. [k] es cuánto llega de las lumbres (de 0 a 1; de día, 1) y [f], de la puerta (el sol, el ocaso o la luna).
 */
internal class AireMeso(val oscuro: Boolean, val noche: Boolean) {
    val lumbre: Boolean get() = oscuro || noche
    val lunar: Boolean get() = oscuro && noche
    val ocaso: Boolean get() = noche && !oscuro
    private val noct: Color get() = if (lunar) MesoTinta.NOCHE_SUENOS else MesoTinta.NOCHE

    /**
     * El color propio de algo, hundido en la noche: su mismo color oscurecido (como multiplicado, así conserva el tono: el azul
     * sigue azul, el cinabrio rojo hondo) y llevado al añil del cuarto, para que lo claro y neutro (el amate, la caliza, el
     * algodón) se hunda en añil y no en gris.
     */
    private fun hondo(c: Color) = Color(c.red * 0.24f + noct.red * 0.4f, c.green * 0.24f + noct.green * 0.4f, c.blue * 0.24f + noct.blue * 0.62f, c.alpha)

    /** Lo que toca la luna: el color de la cosa, apagado y enfriado hacia la plata (con su tono, no un gris). */
    private fun lunado(c: Color) = Color(c.red * 0.4f + 0.1f, c.green * 0.46f + 0.14f, c.blue * 0.5f + 0.3f, c.alpha)

    /** El color de algo con la luz que le llega: [k] de las lumbres, [f] de la puerta. */
    fun c(col: Color, k: Float, f: Float = 0f, calor: Float = 1f): Color {
        if (!oscuro) {
            if (!noche) return col
            return mezcla(mezcla(col, MesoTinta.ATARDECER, 0.32f * (1f - f)), MesoTinta.OCASO, 0.2f * f + 0.08f * k)
        }
        val kk = k.coerceIn(0f, 1f)
        var v = mezcla(hondo(col), col, (1.05f * kk).coerceAtMost(0.94f))
        // Cerca de la lumbre se calienta (sin volver gris el azul: poco y solo muy cerca).
        v = mezcla(v, MesoTinta.LLAMA, (0.1f * kk + 0.22f * kk * kk) * calor)
        val ff = f.coerceIn(0f, 1f) * (1f - 0.7f * kk) * (if (lunar) 0.75f else 0.36f)
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
        oscuro -> MesoTinta.LLAMA.copy(alpha = (a * 1.1f * k).coerceIn(0f, 1f))
        noche -> luzMueble(a * 0.65f)
        else -> luzMueble(a)
    }
    /** El filo frío de la luna en lo que mira a la puerta (de noche). */
    fun helado(a: Float, f: Float): Color = MesoTinta.LUNA.copy(alpha = if (oscuro) (a * f).coerceIn(0f, 1f) else 0f)
    /** La luz de la puerta sobre lo que toca: el sol (de día), el ocaso (Sueños en claro) o la luna (de noche). */
    fun puerta(a: Float): Color = when {
        !lumbre -> MesoTinta.SOL.copy(alpha = a.coerceIn(0f, 1f))
        !oscuro -> MesoTinta.OCASO.copy(alpha = (a * 0.8f).coerceIn(0f, 1f))
        else -> MesoTinta.PLATA.copy(alpha = (a * (if (lunar) 0.55f else 0.26f)).coerceIn(0f, 1f))
    }
    /** El color del ánimo: de noche conserva su color de día y se apaga con la distancia a las lumbres, sin volverse gris. */
    fun animo(t: Color, k: Float, f: Float = 0f): Color {
        if (!oscuro) return c(t, k, f)
        val kk = k.coerceIn(0f, 1f)
        var v = mezcla(Color(t.red * 0.42f + noct.red * 0.18f, t.green * 0.42f + noct.green * 0.18f, t.blue * 0.42f + noct.blue * 0.26f), t, (1.1f * kk).coerceAtMost(0.96f))
        v = mezcla(v, MesoTinta.LLAMA, 0.12f * kk)
        val ff = f.coerceIn(0f, 1f) * (1f - 0.7f * kk) * (if (lunar) 0.5f else 0.3f)
        return if (ff > 0f) mezcla(v, lunado(t), ff) else v
    }
    /**
     * El estuco, el amate o la tela cruda donde va el día: de noche se tiñe de la luz que le toca (ámbar cerca de la lumbre,
     * azulado lejos, plata bajo la luna) sin apagarse tanto que el número no se lea: con la tinta, unos 6 a 1 o más.
     */
    fun etiqueta(k: Float, base: Color = MesoTinta.ESTUCO, f: Float = 0f): Color {
        val kk = k.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(base, MesoTinta.ATARDECER, 0.12f) else base
        val lejos = mezcla(base, Color(0xFF4C5C94), if (lunar) 0.56f else 0.54f)
        val cerca = mezcla(base, MesoTinta.BRASA, 0.4f)
        val v = mezcla(lejos, cerca, (2.2f * kk).coerceAtMost(1f))
        val ff = f.coerceIn(0f, 1f) * (1f - kk) * (if (lunar) 0.5f else 0.2f)
        return if (ff > 0f) mezcla(v, MesoTinta.PLATA, ff) else v
    }
    val tinta: Color get() = MesoTinta.TINTA
    // De noche, más honda: el mes sigue leyéndose (4.5 a 1 o más) sobre el estuco apagado.
    val rubrica: Color get() = if (oscuro) mezcla(MesoTinta.CINABRIO, MesoTinta.TINTA, 0.6f) else MesoTinta.CINABRIO
}

/**
 * El dibujo del estante del diario de Mesoamérica (lo usa `EstanteMeso`, en `BibliotecaAntiguedad.kt`): el cuarto de guardar
 * de la casa de K'an, bajo la bóveda falsa, con el testero de azul maya, la banda de cinabrio, la viga de amarre y el nicho de
 * la lumbre; contra él, el anaquel de chicozapote labrado a hacha de piedra, con su remate de moldura escalonada, las tablas
 * acuñadas en los postes, los amarres de henequén y las zapatas de caliza; a media altura, la tea del poste. Arriba, el
 * canasto de cacao con las mantas, el vaso pintado, el carey, el copal y el ocote; colgadas, las mazorcas y las madejas. Al
 * pie, el fogón de tres piedras y el metate sobre las lajas. El muro, la bóveda y el piso van en `MuebleEstanteMesoMuro.kt`;
 * el anaquel, en `MuebleEstanteMesoAnaquel.kt`; lo de arriba, en `MuebleEstanteMesoPiezas.kt`; el pie y las lumbres, en
 * `MuebleEstanteMesoPie.kt`; lo de los huecos, en `MuebleEstanteMesoHuecos.kt`; la luz, en `MuebleEstanteMesoLuz.kt`; los
 * volúmenes, en `MuebleEstanteMesoVolumenes.kt`.
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
    /** La tea del poste de la izquierda (la tercera lumbre, a media altura): en anchos desde la izquierda. */
    const val TEA_X = 0.063f
    /** En qué hueco va la tea: en el de en medio (con menos de tres huecos, no hay: el fogón ya alcanza). */
    fun teaHueco(repisas: Int): Int = if (repisas < 3) -1 else repisas / 2
    /** La altura de la tea en ese hueco, en fracción de su alto desde arriba. */
    const val TEA_EN_HUECO = 0.3f
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
        // La corona se guarda en una imagen: se pinta sin la tea (que cambia con los huecos y no le llega).
        val mc = MesaMeso(a, w, ch, b.top, h)
        val tea = d.repisas.getOrNull(teaHueco(d.repisas.size))?.let { r -> Offset(w * TEA_X, r.top + r.height * TEA_EN_HUECO) }
        val m = if (tea == null) mc else MesaMeso(a, w, ch, b.top, h, tea)
        val clave = "$w|$ch|${b.top}|$fondo|$h|$density|$oscuro|$noche"
        val ranura = if (noche) 1 else 0
        // ---- arriba (pintado una vez por tamaño y modo): el testero, la bóveda, la viga, lo colgado, el remate y el bodegón
        val corona: DrawScope.() -> Unit = {
            testeroMeso(mc)
            bovedaMeso(mc)
            nichoMeso(mc)
            colgadoMeso(mc)
            remateMeso(mc)
            bodegonMeso(mc)
            if (a.lumbre) lumbreNichoMeso(mc)
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
        clipRect(0f, ch, w, fondo) { fondosHuecosMeso(m, d.repisas, d.ocupado) }
        if (d.tablas.isNotEmpty()) filaJuntaMeso(m, fondo, 0f, w)
        tablasMeso(m, d)
        clipRect(0f, ch, w, fondo) { postesMeso(m, ch, fondo) }
        amarresMeso(m, d)
        m.poste?.let { teaMeso(m, it) }
        d.placas.forEach { p -> rotuloMeso(m, p, medidor) }
        val usados = HashSet<Int>()
        d.repisas.forEachIndexed { i, r -> d.ocupado.getOrNull(i)?.let { oc -> huecoLibreMeso(m, i, r, oc, usados) } }
        granoMueble(Rect(0f, ch, w, fondo), oscuro)
        // ---- abajo: el zócalo, las zapatas, el piso de lajas, el fogón y el metate. Se pinta en cada dibujo (unas 370
        // operaciones): guardarlo también en una imagen pasaría de unos 3 MB por modo, con la corona.
        clipRect(0f, fondo, w, h) {
            pisoMeso(m, fondo)
            patasMeso(m, fondo)
            pieMeso(m)
            if (a.lumbre) lumbreFogonMeso(m)
            granoMueble(Rect(0f, fondo, w, h), oscuro)
        }
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) = volumenMeso(v, medidor, oscuro)
}

/** Una lumbre de la noche: dónde arde y de qué clase es (0, la del nicho; 1, la del fogón; 2, la tea del poste). */
internal class LumbreMeso(val centro: Offset, val clase: Int)

/**
 * Lo que comparten las piezas del estante en un dibujo: el aire, el ancho, dónde acaba la corona, dónde empieza la base y el
 * alto, y dónde arde la tea del poste ([poste], si la hay); ya calculados, las lumbres, el piso, los postes y la forma de la
 * mancha de la puerta.
 */
internal class MesaMeso(val a: AireMeso, val w: Float, val ch: Float, val base: Float, val alto: Float, val poste: Offset? = null) {
    /** La lumbre del nicho y la del fogón, en píxeles. */
    val nicho = Offset(w * MuebleEstanteMeso.NICHO_X, w * MuebleEstanteMeso.NICHO_Y)
    val fogon = Offset(w * MuebleEstanteMeso.FOGON_X, base + w * MuebleEstanteMeso.FOGON_BAJO)
    /** La tea, encendida solo de noche fuera de los Sueños (en los Sueños manda la luna; de día está apagada). */
    val tea: Offset? = if (a.oscuro && !a.lunar) poste else null
    /** Las lumbres que alumbran de noche. */
    val lumbres: List<LumbreMeso> = listOfNotNull(LumbreMeso(nicho, 0), LumbreMeso(fogon, 1), tea?.let { LumbreMeso(it, 2) })
    /** Donde empieza el piso (la junta del zócalo con las lajas). */
    val piso: Float = base + w * MuebleEstanteMeso.PISO
    /** Los postes: el de la izquierda y el de la derecha (de bajo el remate a las zapatas). */
    val posteI = Rect(w * (MuebleEstanteMeso.LADO - 0.07f), ch - 1f, w * MuebleEstanteMeso.LADO, piso - w * 0.03f)
    val posteD = Rect(w * (1f - MuebleEstanteMeso.LADO), ch - 1f, w * (1.07f - MuebleEstanteMeso.LADO), piso - w * 0.03f)
    /** Dónde pisa lo de arriba. */
    val apoyo: Float = w * MuebleEstanteMeso.APOYO
    /** La mancha de la puerta en el testero, en píxeles (de día el sol; en los Sueños claros, el ocaso). */
    val puerta: Path = formaPuertaMeso(w, a.ocaso)
    /** El plano de luna de los Sueños de noche (solo se arma ahí). */
    val luna: Path? = if (a.lunar) formaLunaMeso(w) else null
    /** La parte de la mancha de la puerta que cae en [r] (un camino ya cortado: pintarlo es más barato que recortar). */
    fun puertaEn(r: Rect): Path = Path().apply { op(puerta, Path().apply { addRect(r) }, PathOperation.Intersect) }

    /** Lo que llega del nicho y del fogón a un punto, cada una por separado. */
    fun k1(p: Offset): Float = nichoLuzMeso((p.x - nicho.x) / w, (p.y - nicho.y) / w, a.lunar)
    fun k2(p: Offset): Float = fogonLuzMeso((p.x - fogon.x) / w, (p.y - fogon.y) / w, a.lunar)
    fun k3(p: Offset): Float = tea?.let { teaLuzMeso((p.x - it.x) / w, (p.y - it.y) / w) } ?: 0f
    /** Cuánto llega de las lumbres a un punto (de día, todo). */
    fun k(p: Offset): Float = if (a.lumbre) juntarLucesMeso(juntarLucesMeso(k1(p), k2(p)), k3(p)) else 1f
    /** Cuánto llega de la puerta (el sol de día, el ocaso en los Sueños claros, la luna de noche). */
    fun f(p: Offset): Float = if (a.oscuro) lunaMeso(p.x / w, p.y / w, a.lunar) else luzPuertaMeso(p.x / w, p.y / w, a.ocaso)
    /** De noche, si la luz le llega a algo en [x], [y] por la derecha (de la lumbre que más le alumbra). */
    fun der(x: Float, y: Float): Boolean {
        if (!a.oscuro) return false
        return x < lumbreDe(Offset(x, y)).x - w * 0.02f
    }
    /** La lumbre que más alumbra un punto (para las sombras de noche). */
    fun lumbreDe(p: Offset): Offset {
        val c1 = k1(p); val c2 = k2(p); val c3 = k3(p)
        return if (c3 > c1 && c3 > c2) tea ?: nicho else if (c1 > c2) nicho else fogon
    }
}

// ---------------------------------------------------------------- lo fijo pintado una vez

/**
 * Lo fijo pintado una vez: dos imágenes (la corona del diario y la de los Sueños), cada una con la clave de su tamaño y su
 * modo; si la clave cambia, se vuelve a pintar. Pegar una imagen cuesta mucho menos que repetir el muro, la bóveda y el
 * bodegón en cada dibujo. La corona no crece con las repisas (unos 2.5 MB a 1080 px de pantalla).
 */
internal object CapaMeso {
    private val claves = arrayOfNulls<String>(2)
    private val imagenes = arrayOfNulls<ImageBitmap>(2)

    /**
     * Pega la imagen de [r] (la pinta antes si su clave cambió). [debajo], si va, se pinta en una imagen que sigue hasta
     * [abajo], debajo de la de [r] tal cual: es lo que cubre la fila de la junta que [r] corta a medio píxel.
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
    // Cinco franjas que no se pisan (donde dos capas se pisarían, una sola con las dos juntas): menos pintura que encimarlas.
    fun junta(c1: Color, c2: Color) = c1.copy(alpha = 1f - (1f - c1.alpha) * (1f - c2.alpha))
    val luz = a.b(0.2f * fuerza, k); val sombra = a.s(0.2f * fuerza)
    drawRect(luz, Offset(x(0f, 0.12f), r.top), Size(r.width * 0.12f, r.height))
    drawRect(junta(luz, a.b(0.32f * fuerza, k)), Offset(x(0.12f, 0.08f), r.top), Size(r.width * 0.08f, r.height))
    drawRect(luz, Offset(x(0.2f, 0.1f), r.top), Size(r.width * 0.1f, r.height))
    drawRect(sombra, Offset(x(0.6f, 0.26f), r.top), Size(r.width * 0.26f, r.height))
    drawRect(junta(sombra, a.s(0.16f * fuerza)), Offset(x(0.86f, 0.14f), r.top), Size(r.width * 0.14f, r.height))
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
            translate(w * 0.014f * dx, dy * 0.6f) { drawPath(p, MesoTinta.NOCHE_SOMBRA.copy(alpha = (0.45f * k).coerceAtMost(0.26f))) }
        }
    }
}

/** Un número de 0 a 1 con la semilla del tema, para que las piezas de arriba no repitan las de otros. */
internal fun azarMeso(semilla: Int, k: Int): Float = azar(semilla * 31 + 1707, k)
