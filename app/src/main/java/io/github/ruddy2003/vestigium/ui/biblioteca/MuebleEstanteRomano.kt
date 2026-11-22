package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * La paleta del estante de Romano (sale del cuadro de Cayo, el legionario del Danubio): la cal del cuarto del escribiente
 * del campamento y su sombra fría, el almagre de las líneas pintadas, el ladrillo cocido de los pilares y su mortero, el
 * roble de la viga y de las tablas, el hierro, el bronce, el aliso pálido de las hojas de escribir, el hueso y el trigo. Dos
 * temperaturas: lo cálido es la luz (el sol de la ventana, la linterna, el ladrillo, el trigo); lo frío, la sombra de la
 * cal, el hierro, el basalto de la muela y el espejuelo de la ventana.
 */
internal object PaletaRomano {
    /** La cal del muro: su cara, la que da el sol y la que queda a la sombra (gris azulada). */
    val CAL = Color(0xFFE6DDCB)
    val CAL_CLARA = Color(0xFFF4EEE1)
    val CAL_SOMBRA = Color(0xFF8F9496)
    /** La sombra fría de la cal y del hierro. */
    val FRIO = Color(0xFF33405A)
    /** El almagre de las líneas pintadas y del zócalo. */
    val ALMAGRE = Color(0xFF94402A)
    val ALMAGRE_OSC = Color(0xFF6A2A1C)
    /** El ladrillo cocido romano, apagado y desigual: el rojo tostado, el ocre, el pasado de horno; el mortero de arena. */
    val LADRILLO = Color(0xFF9C6A50)
    val LADRILLO_CLARO = Color(0xFFB48C68)
    val LADRILLO_OSC = Color(0xFF6E4A3C)
    val MORTERO = Color(0xFFBFAE8E)
    /** La terra sigillata: el rojo con brillo de los cuencos de mesa. */
    val SIGILLATA = Color(0xFFA9452C)
    /** El roble de la viga y de las tablas: su cara, su luz y lo hondo. */
    val ROBLE = Color(0xFF86613F)
    val ROBLE_LUZ = Color(0xFFB08A5E)
    val ROBLE_OSC = Color(0xFF45301F)
    /** El hierro forjado y el bronce. */
    val HIERRO = Color(0xFF2B2E37)
    val HIERRO_LUZ = Color(0xFF7B818D)
    val BRONCE = Color(0xFFA5773C)
    val BRONCE_LUZ = Color(0xFFDDB574)
    /** El aliso de las hojas de escribir, el hueso de las placas y la tinta de hollín. */
    val ALISO = Color(0xFFE6D4AA)
    val ALISO_OSC = Color(0xFFAE9164)
    val HUESO = Color(0xFFF1E8D2)
    val TINTA = Color(0xFF221710)
    /** El cuero sin teñir, el cordel de cáñamo y el plomo de los sellos. */
    val CUERO = Color(0xFF8A5A36)
    val CANAMO = Color(0xFFB89E70)
    val PLOMO = Color(0xFF6E7176)
    /** El basalto de la muela, el trigo, el pan y el barro del ánfora. */
    val BASALTO = Color(0xFF5A5D63)
    val TRIGO = Color(0xFFD5A653)
    val PAN = Color(0xFFB9783E)
    val BARRO = Color(0xFFB36A4A)
    /** El espejuelo de la ventana (la piedra traslúcida de Hispania) y el cuerno de la linterna. */
    val ESPEJUELO = Color(0xFFDCE3DF)
    val CUERNO = Color(0xFFC8984C)
    /** El aire de la noche, el de los Sueños y el anochecer de los Sueños en claro; el crepúsculo en la ventana. */
    val NOCHE = Color(0xFF120F1A)
    val NOCHE_SUENOS = Color(0xFF0E1330)
    val ANOCHECER = Color(0xFF3A2D5C)
    val CREPUSCULO = Color(0xFFE59A68)
    /** La llama de la linterna, su corazón y la luna. */
    val LLAMA = Color(0xFFFFB25C)
    val FUEGO = Color(0xFFFFE2A6)
    val LUNA = Color(0xFFE6EAF4)
    /** El roce de la luna en los cantos, de noche. */
    val LUNA_RAS = Color(0xFF9FB2D8)
    /** Lo hondo del tinte: el ánimo se tiñe un poco hacia aquí para que sea cuero o pintura y no pastel. */
    val TINTE_HONDO = Color(0xFF22150F)

    val LETRA: FontFamily = Letra.inscripcion
}

/**
 * El aire del momento en el estante de Romano: de día (la luz de la ventana de espejuelo), de noche (el modo oscuro: solo
 * la linterna de cuerno) y en los Sueños ([noche]: al anochecer en claro, la noche azul en oscuro, con la luna detrás del
 * espejuelo). De él salen el color de cada cosa según cuánta luz de la linterna le llega, la sombra, la luz y el filo.
 */
internal class AireRomano(val oscuro: Boolean, val noche: Boolean) {
    private val p = PaletaRomano
    /** Si arde la linterna. */
    val lumbre: Boolean get() = oscuro || noche
    /** El color en que se hunde lo que no se alumbra. */
    val hondo: Color get() = if (oscuro) (if (noche) p.NOCHE_SUENOS else p.NOCHE) else SOMBRA_MUEBLE

    fun luz(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(mezcla(c, p.ANOCHECER, 0.44f - 0.34f * k), p.LLAMA, 0.16f * k) else c
        return mezcla(mezcla(c, hondo, 0.8f - 0.82f * k.pow(0.85f)), p.LLAMA, 0.3f * k)
    }
    /** La segunda luz de la noche: la luna difusa del espejuelo, fría, que roza los cantos que la miran (la izquierda). */
    fun lunar(a: Float): Color = if (oscuro) p.LUNA_RAS.copy(alpha = a.coerceIn(0f, 1f)) else Color.Transparent
    fun sombra(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)
    /** La sombra fría: la de la cal y el hierro (de noche, la misma que las demás). */
    fun frio(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else p.FRIO.copy(alpha = a.coerceIn(0f, 1f))
    fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.12f else if (noche) a * 0.5f else a)
    /** El filo de la linterna en lo que la mira; de día no hay. */
    fun filo(a: Float): Color = p.LLAMA.copy(alpha = if (oscuro) a.coerceIn(0f, 1f) else if (noche) (a * 0.4f).coerceIn(0f, 1f) else 0f)
    /** El color del ánimo: de noche se apaga menos que lo demás, para que se siga viendo en la repisa más lejana. */
    fun animo(t: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(t, p.ANOCHECER, 0.16f) else t
        return mezcla(mezcla(t, hondo, 0.58f - 0.56f * k.pow(0.8f)), p.LLAMA, 0.12f * k)
    }
    /** El hueso o la madera clara donde va el día: casi no se apaga, para que el número se lea siempre. */
    fun etiqueta(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(c, p.ANOCHECER, 0.12f) else c
        return mezcla(mezcla(c, hondo, 0.42f - 0.36f * k), p.LLAMA, 0.16f * k)
    }
}

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoRomano(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** [p] corrido. */
internal fun corridoRomano(p: Path, dx: Float, dy: Float) = Path().apply { addPath(p, Offset(dx, dy)) }

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz. */
internal fun DrawScope.contactoRomano(centro: Offset, ancho: Float, alto: Float, a: AireRomano, fuerza: Float = 0.34f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.3f else fuerza), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

/** Un clavo de forja visto de frente: su sombra, la cabeza y el punto de luz (y el filo de la linterna de noche). */
internal fun DrawScope.clavoRomano(p: Offset, r: Float, a: AireRomano, k: Float, filo: Float = 0f) {
    drawCircle(a.frio(0.4f), r * 1.05f, p + Offset(r * 0.35f, r * 0.45f))
    drawCircle(a.luz(PaletaRomano.HIERRO, k), r, p)
    drawCircle(a.claro(0.45f), r * 0.36f, p - Offset(r * 0.3f, r * 0.3f))
    if (a.oscuro && filo > 0.05f) drawCircle(a.filo(0.7f * filo), r * 0.4f, p + Offset(r * 0.25f, -r * 0.25f))
}

/**
 * El dibujo del estante del diario de Romano (lo usa `EstanteRomano`, en `BibliotecaAntiguedad.kt`): la estantería de obra
 * del cuarto del escribiente (el tabularium) en el cuartel general de un campamento del Danubio, hacia el 110 d. C. Dos
 * pilares de ladrillo cocido, levantados por los mismos soldados, con tablas gruesas de roble encajadas y una viga encima,
 * contra un muro encalado con sus líneas de almagre; arriba a la izquierda, la ventana chica de espejuelo. Todo el dibujo sale
 * de [estante] y de [volumen]; la luz de la noche de los dos sale de la misma curva, [alcance]: la de la linterna de cuerno
 * colgada a la derecha. El muro, la ventana y el suelo están en [MuroEstanteRomano], la obra (pilares, tablas, viga) en
 * [ObraEstanteRomano], los objetos en [PiezasEstanteRomano] y las entradas en [VolumenEstanteRomano].
 */
internal object MuebleEstanteRomano {
    private val P = PaletaRomano

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.5f
    const val BASE = 0.23f
    const val LADO = 0.085f
    val ALTO_REPISA: Dp = 110.dp
    val TABLA: Dp = 17.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (26 + (palabras / 40).coerceAtMost(18)).dp

    /** Cuál de los tres es una entrada: 0, el legajo de hojas de madera; 1, la arqueta herrada; 2, la bolsa de cuero. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod((semilla xor (semilla ushr 7)) * 31 + (semilla ushr 3), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** Llenan unos cuatro quintos de la repisa: el legajo, el más alto; la bolsa, mediana; la arqueta, baja y ancha. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.85f + 0.08f * azar(semilla, 1)
        // La arqueta es más ancha que alta: su caja, de unos cuatro quintos de su ancho, más la tapa abierta encima.
        1 -> ((ancho(palabras).value * 0.92f).coerceIn(30f, 46f) * 1.5f) / ALTO_REPISA.value
        else -> 0.62f + 0.1f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde la linterna, en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val LAMP_X = 0.855f
    const val LAMP_Y = 0.33f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    private const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    private const val FILA = 127f
    /** En la base: dónde empieza el suelo, en fracción de su alto. */
    const val SUELO = 0.5f
    /** La viga de arriba, en anchos. */
    const val VIGA = 0.05f

    /**
     * La curva de la luz de la linterna, la misma para el mueble y para los volúmenes: cuánta llega (de 1 a 0) a un punto que
     * está a [dx] y [dy] de la llama, en anchos del mueble. Cae deprisa: la linterna alumbra el rincón de la derecha y la
     * repisa de arriba; en un estante corto se acaba antes, para que la última repisa quede siempre en penumbra.
     */
    fun alcance(dx: Float, dy: Float, repisas: Int): Float {
        val radio = 0.95f + 0.12f * repisas.coerceIn(2, 5)
        return (1f - sqrt(dx * dx * 1.1f + dy * dy) / radio).coerceIn(0f, 1f).pow(1.3f)
    }

    /** La luz de la linterna en el muro de arriba: la cal la recoge bien, pero el rincón de la ventana queda en lo oscuro. */
    fun alcanceMuro(dx: Float, dy: Float): Float = (1f - sqrt(dx * dx * 1.2f + dy * dy) / 0.8f).coerceIn(0f, 1f).pow(1.2f)

    /** La luz de la linterna en lo de arriba (la viga y lo que hay en ella). */
    fun alcanceArriba(dx: Float, dy: Float): Float = (1f - sqrt(dx * dx * 1.1f + dy * dy) / 1.0f).coerceIn(0f, 1f).pow(1.25f)

    /** La luz que llega al cuerpo del mueble: [pos] cuenta repisas desde la de arriba (1.5 es media altura de la segunda). */
    fun cercaCuerpo(pos: Float, repisas: Int, fx: Float): Float =
        alcance(fx - LAMP_X, CORONA - LAMP_Y + pos * FILA / ANCHO_NOMINAL, repisas)

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireRomano(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val b = d.base
        val suelo = b.top + b.height * SUELO
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * u
        val llama = Offset(w * LAMP_X, w * LAMP_Y)
        val cerca: (Offset) -> Float = { p ->
            if (p.y <= ch) alcanceArriba((p.x - llama.x) / w, (p.y - llama.y) / w) else cercaCuerpo((p.y - ch) / maxOf(1f, fila), n, p.x / w)
        }
        val cercaMuro: (Offset) -> Float = { p -> alcanceMuro((p.x - llama.x) / w, (p.y - llama.y) / w) }
        val apoyo = ch - w * VIGA

        with(MuroEstanteRomano) {
            muro(a, ch, suelo, cercaMuro)
            ventana(a)
        }
        if (oscuro) {
            mancha(llama, w * 0.6f, P.LLAMA.copy(alpha = 0.2f))
            mancha(llama, w * 0.2f, P.FUEGO.copy(alpha = 0.28f))
        } else if (noche) {
            // Al anochecer la linterna está recién encendida: un charco chico.
            mancha(llama, w * 0.22f, P.LLAMA.copy(alpha = 0.34f))
        }
        with(MuroEstanteRomano) { espiga(a, suelo, cerca) }
        with(ObraEstanteRomano) {
            d.repisas.forEachIndexed { k, r -> fondoRepisa(k, r, a, cerca) }
            d.tablas.forEachIndexed { k, r -> tabla(r, 610 + 7 * k, a, cerca) }
            bajo(Rect(d.izquierda.right, b.top, d.derecha.left, suelo), a)
            d.placas.forEach { p -> letrero(p, medidor, a, cerca(p.r.center)) }
            pilar(Rect(d.izquierda.left, apoyo, d.izquierda.right, suelo), 11, a, cerca)
            pilar(Rect(d.derecha.left, apoyo, d.derecha.right, suelo), 23, a, cerca)
            viga(Rect(0f, apoyo, w, ch), a, cerca)
        }
        with(PiezasEstanteRomano) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, a, cerca(Offset((oc + r.right) / 2, r.center.y)))
            }
            arriba(a, apoyo, cerca)
            alPie(a, suelo, cerca)
            linterna(a)
        }
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireRomano(oscuro, v.noche)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val k = if (a.lumbre) cercaCuerpo(v.repisa + 0.55f, v.repisas, fx) else 1f
        // De qué lado le llega la luz: de día, de la ventana (izquierda); de noche, de la linterna.
        val luzDe = if (a.oscuro && fx > LAMP_X) -1f else if (a.oscuro) 1f else -1f
        with(VolumenEstanteRomano) {
            when (clase(v.semilla)) {
                0 -> legajo(v, medidor, a, k, luzDe)
                1 -> arqueta(v, medidor, a, k, luzDe)
                else -> bolsa(v, medidor, a, k, luzDe)
            }
        }
    }
}
