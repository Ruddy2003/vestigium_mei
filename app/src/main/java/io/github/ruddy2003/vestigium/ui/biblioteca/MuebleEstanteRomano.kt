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
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.exp
import kotlin.math.sqrt

/**
 * La paleta del estante de Romano: el cuarto de los oficiales del cuartel de un campamento del Danubio hacia el 110 d. C.,
 * pintado como se pintaban: la pared de ocre amarillo con filetes de cinabrio y blanco y el zócalo negro jaspeado; las
 * pilastras y la cornisa de estuco blanco; el fondo de los casilleros de negro pompeyano; el roble de las tablas, el bronce y
 * el hierro de los herrajes; el cuero, el aliso y el hueso de lo guardado. Dos temperaturas: lo cálido es la luz y el ocre;
 * lo frío, el espejuelo de la ventana, el hierro y la luna.
 */
internal object PaletaRomano {
    /** La pared de ocre amarillo: su cara, su luz y su sombra. */
    val OCRE = Color(0xFFC9973E)
    val OCRE_CLARO = Color(0xFFE3BE6E)
    val OCRE_OSC = Color(0xFF94661F)
    /** El cinabrio de los filetes y del friso, y el blanco de cal de las líneas finas. */
    val CINABRIO = Color(0xFFA9341F)
    val CINABRIO_OSC = Color(0xFF6E2014)
    val CAL = Color(0xFFF1EBDD)
    /** El negro pompeyano del zócalo y de los casilleros, y su luz. */
    val NEGRO = Color(0xFF221A17)
    val NEGRO_LUZ = Color(0xFF3E322B)
    /** El estuco blanco de las pilastras y la cornisa, su sombra y su veta de mármol fingido. */
    val ESTUCO = Color(0xFFEEE5D1)
    val ESTUCO_SOMBRA = Color(0xFFBDAE93)
    val ESTUCO_VETA = Color(0xFFB09E80)
    /** El roble de las tablas: su cara, su luz y lo hondo. */
    val ROBLE = Color(0xFF7E5636)
    val ROBLE_LUZ = Color(0xFFAD8256)
    val ROBLE_OSC = Color(0xFF3F2817)
    /** El hierro forjado y el bronce. */
    val HIERRO = Color(0xFF34353B)
    val HIERRO_LUZ = Color(0xFF8A8E98)
    val BRONCE = Color(0xFFB07A3A)
    val BRONCE_LUZ = Color(0xFFEBC273)
    val BRONCE_OSC = Color(0xFF6A4520)
    /** El aliso de las hojas de escribir, el hueso de las placas y la tinta de hollín. */
    val ALISO = Color(0xFFEAD9AE)
    val ALISO_OSC = Color(0xFFB19466)
    val HUESO = Color(0xFFF2EAD5)
    val TINTA = Color(0xFF211610)
    /** El cuero sin teñir, el cordel de cáñamo y el plomo de los sellos. */
    val CUERO = Color(0xFF8E5C35)
    val CANAMO = Color(0xFFC2A675)
    val PLOMO = Color(0xFF6E7176)
    /** El ladrillo del suelo y de los desconchones, apagado y desigual, y su mortero de arena. */
    val LADRILLO = Color(0xFFA0603F)
    val LADRILLO_CLARO = Color(0xFFBF8158)
    val LADRILLO_OSC = Color(0xFF713F2A)
    val MORTERO = Color(0xFFC6B28D)
    /** El basalto de la muela, el trigo, el pan, el barro del ánfora y la terra sigillata. */
    val BASALTO = Color(0xFF55585F)
    val TRIGO = Color(0xFFD8A851)
    val PAN = Color(0xFFB9783E)
    val BARRO = Color(0xFFB66C49)
    val SIGILLATA = Color(0xFFAE4428)
    /** La lana roja del sagum, doblado sobre la cornisa. */
    val LANA = Color(0xFF8C3526)
    /** El espejuelo de la ventana y el cuerno de la linterna. */
    val ESPEJUELO = Color(0xFFE4E3D0)
    val CUERNO = Color(0xFFC8984C)
    /** El aire de la noche (cálido y hondo), el de los Sueños, el anochecer de los Sueños en claro y su crepúsculo. */
    val NOCHE = Color(0xFF160F14)
    val NOCHE_SUENOS = Color(0xFF0F1230)
    val ANOCHECER = Color(0xFF3A2D5C)
    val CREPUSCULO = Color(0xFFE79A62)
    /** La llama de la linterna, su corazón, la luna y el roce de la luna en los cantos. */
    val LLAMA = Color(0xFFFFB45C)
    val FUEGO = Color(0xFFFFE4AA)
    val LUNA = Color(0xFFE6EAF4)
    val LUNA_RAS = Color(0xFF9FB2D8)
    /** La sombra fría de día (gris azulado), para el hierro y el espejuelo. */
    val FRIO = Color(0xFF33405A)
    /** Lo hondo del tinte: el ánimo se tiñe un poco hacia aquí para que sea cuero o pintura y no pastel. */
    val TINTE_HONDO = Color(0xFF22150F)

    val LETRA: FontFamily = Letra.inscripcion
}

/**
 * El aire del momento en el estante de Romano: de día (la luz difusa de la ventana de espejuelo), de noche (el modo oscuro:
 * arde la linterna de cuerno) y en los Sueños ([noche]: al anochecer en claro, la noche azul en oscuro). Como en el Ideario
 * del tema, de noche cada color va de su hondo (oscuro, de su mismo tono, con el aire de la noche) a su color según cuánta
 * luz le llega ([cerca], de 0 a 1), con el calor de la llama encima.
 */
internal class AireRomano(val oscuro: Boolean, val noche: Boolean) {
    private val p = PaletaRomano
    /** Si arde la linterna. */
    val lumbre: Boolean get() = oscuro || noche
    /** El aire en que se hunde lo que no se alumbra. */
    val hondo: Color get() = if (oscuro) (if (noche) p.NOCHE_SUENOS else p.NOCHE) else sombraMueble(1f)
    /** El velo de la noche sobre la obra: un pardo violáceo cálido (en los Sueños, más azul), no un gris. */
    val velo: Color get() = if (noche) mezcla(p.NOCHE_SUENOS, Color(0xFF34284A), 0.5f) else Color(0xFF2E1B26)

    /** El color propio de algo, hundido: oscuro, de su mismo tono, con el aire de la noche. */
    fun hundido(c: Color): Color {
        val n = hondo
        return Color(c.red * 0.16f + n.red * 0.8f, c.green * 0.16f + n.green * 0.8f, c.blue * 0.16f + n.blue * 0.8f, c.alpha)
    }

    fun luz(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(mezcla(c, p.ANOCHECER, 0.44f - 0.34f * k), p.LLAMA, 0.16f * k) else c
        return mezcla(mezcla(hundido(c), c, (1.05f * k).coerceAtMost(0.92f)), p.LLAMA, 0.16f * k)
    }
    fun sombra(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)
    /** La sombra fría del día (la de la cal, el hierro y el espejuelo); de noche, la misma que las demás. */
    fun frio(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else p.FRIO.copy(alpha = a.coerceIn(0f, 1f))
    /** La luz crema del día; de noche, casi nada (el brillo de la noche lo pone [brillo]). */
    fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.1f else if (noche) a * 0.5f else a)
    /** El brillo de lo que mira a la luz: de día crema; de noche, el filo de la llama según [cerca]. */
    fun brillo(a: Float, cerca: Float): Color =
        if (!oscuro) luzMueble(if (noche) a * 0.55f else a) else p.LLAMA.copy(alpha = (a * 0.95f * cerca).coerceIn(0f, 1f))
    /** El filo de la linterna en lo que la mira; de día no hay. */
    fun filo(a: Float): Color = p.LLAMA.copy(alpha = if (oscuro) a.coerceIn(0f, 1f) else if (noche) (a * 0.4f).coerceIn(0f, 1f) else 0f)
    /** La segunda luz de la noche: la luna difusa del espejuelo, fría, que roza los cantos que la miran (la izquierda). */
    fun lunar(a: Float): Color = if (oscuro) p.LUNA_RAS.copy(alpha = a.coerceIn(0f, 1f)) else Color.Transparent
    /** El color del ánimo: de noche no baja de un tercio de su color, para que el cuerpo se siga viendo en la repisa más lejana. */
    fun animo(t: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(t, p.ANOCHECER, 0.16f) else t
        return mezcla(mezcla(hundido(t), t, (0.34f + 0.85f * k).coerceAtMost(0.95f)), p.LLAMA, 0.12f * k)
    }
    /** El hueso o la madera clara donde va el día: casi no se apaga, para que el número se lea siempre. */
    fun etiqueta(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(c, p.ANOCHECER, 0.12f) else c
        return mezcla(mezcla(hundido(c), c, 0.62f + 0.38f * k), p.LLAMA, 0.14f * k)
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

/** Un clavo de cabeza redonda visto de frente: su sombra, la cabeza ([metal]) y el punto de luz. */
internal fun DrawScope.remacheRomano(p: Offset, r: Float, a: AireRomano, k: Float, metal: Color = PaletaRomano.HIERRO) {
    drawCircle(a.sombra(0.4f), r * 1.05f, p + Offset(r * 0.4f, r * 0.5f))
    drawCircle(a.luz(metal, k), r, p)
    drawCircle(a.brillo(0.55f, k), r * 0.38f, p - Offset(r * 0.3f, r * 0.3f))
}

/**
 * El dibujo del estante del diario de Romano (lo usa `EstanteRomano`, en `BibliotecaAntiguedad.kt`): la estantería de obra
 * del cuarto de los oficiales del cuartel de un campamento del Danubio, hacia el 110 d. C., hecha con oficio: dos pilastras de
 * estuco blanco con su basa, su capitel y su panel con filete; la cornisa de estuco moldurada con el friso pintado; las tablas
 * gruesas de roble con el canto moldurado y sus ménsulas de bronce; el fondo de cada casillero de negro pompeyano con su
 * filete; el podio negro jaspeado. Contra la pared de ocre amarillo con sus paños y filetes y la ventana chica de espejuelo.
 * Todo sale de [estante] y [volumen]; la luz de la noche de los dos sale de la misma curva, [alcance]: la de la linterna de
 * cuerno colgada a la derecha. La pared, la ventana y el suelo están en [MuroEstanteRomano]; la obra, en [ObraEstanteRomano];
 * los objetos, en [PiezasEstanteRomano] y [CoronaEstanteRomano]; las entradas, en [VolumenEstanteRomano].
 */
internal object MuebleEstanteRomano {
    private val P = PaletaRomano

    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteRomano` las lee al dibujar)

    val CORONA = 0.6f
    val BASE = 0.3f
    val LADO = 0.1f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 20.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (30 + (palabras / 40).coerceAtMost(18)).dp

    /** Cuál de los tres es una entrada: 0, el legajo de hojas en su funda; 1, la arqueta herrada; 2, la bolsa de cuero. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod((semilla xor (semilla ushr 7)) * 31 + (semilla ushr 3), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** Llenan de cuatro quintos a nueve décimos del casillero: el legajo, el más alto; las arquetas apiladas; la bolsa, más baja. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.86f + 0.07f * azar(semilla, 1)
        1 -> 0.84f + 0.06f * azar(semilla, 1)
        else -> 0.8f + 0.07f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde la linterna, en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val LAMP_X = 0.86f
    const val LAMP_Y = 0.34f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    private const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    private const val FILA = 132f
    /** En la base: dónde acaba el podio y empieza el suelo, en fracción de su alto. */
    const val SUELO = 0.36f
    /** La cornisa de estuco, en anchos: lo que mide de alto, desde donde pisan las cosas hasta el arranque de las repisas. */
    const val CORNISA = 0.115f

    /**
     * La curva de la luz de la linterna, la misma para el mueble y para los volúmenes: cuánta llega (de 1 a 0) a un punto que
     * está a [dx] y [dy] de la llama, en anchos del mueble. Cae como en el Ideario: encendido cerca, a medias en la primera
     * repisa, tenue en la segunda y casi perdido abajo; lo de la izquierda, un poco antes.
     */
    fun alcance(dx: Float, dy: Float): Float =
        (1.3f * exp(-2.7f * sqrt(dx * dx * 1.15f + dy * dy * (if (dy > 0f) 0.9f else 1f)))).coerceIn(0.03f, 1f)

    /** La luz que llega al cuerpo del mueble: [pos] cuenta repisas desde la de arriba (1.5 es media altura de la segunda). */
    fun cercaCuerpo(pos: Float, fx: Float): Float =
        alcance(fx - LAMP_X, CORONA - LAMP_Y + pos * FILA / ANCHO_NOMINAL)

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireRomano(oscuro, noche)
        val ch = d.corona.bottom
        val b = d.base
        val suelo = b.top + b.height * SUELO
        val fila = if (d.repisas.isNotEmpty()) d.repisas[0].height + d.tablas[0].height else FILA * density
        val llama = Offset(w * LAMP_X, w * LAMP_Y)
        val cerca: (Offset) -> Float = { p ->
            if (p.y <= ch) alcance((p.x - llama.x) / w, (p.y - llama.y) / w)
            else cercaCuerpo((p.y - ch) / maxOf(1f, fila), p.x / w)
        }
        val apoyo = ch - w * CORNISA

        with(MuroEstanteRomano) {
            muro(a, apoyo, suelo, cerca)
            ventana(a)
            espiga(a, suelo, cerca)
        }
        with(CoronaEstanteRomano) { pared(a, apoyo, cerca) }
        with(ObraEstanteRomano) {
            d.repisas.forEachIndexed { k, r -> casillero(k, r, a, cerca) }
            podio(Rect(0f, b.top, w, suelo), a, cerca)
            pilastra(Rect(d.izquierda.left, ch, d.izquierda.right, b.top), true, a, cerca)
            pilastra(Rect(d.derecha.left, ch, d.derecha.right, b.top), false, a, cerca)
            d.tablas.forEachIndexed { k, r -> tabla(r, 610 + 7 * k, a, cerca) }
            d.placas.forEach { p -> letrero(p, medidor, a, cerca(p.r.center)) }
            cornisa(Rect(0f, apoyo, w, ch), a, cerca)
        }
        with(PiezasEstanteRomano) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, a, cerca(Offset((oc + r.right) / 2, r.center.y)))
            }
            alPie(a, suelo, cerca)
        }
        with(CoronaEstanteRomano) { encima(a, apoyo, cerca) }
        if (oscuro) {
            mancha(llama + Offset(-w * 0.04f, w * 0.06f), w * 0.5f, P.LLAMA.copy(alpha = 0.1f))
            mancha(llama, w * 0.16f, P.LLAMA.copy(alpha = 0.24f))
        } else if (noche) {
            mancha(llama, w * 0.2f, P.LLAMA.copy(alpha = 0.3f))
        }
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireRomano(oscuro, v.noche)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val k = if (a.lumbre) cercaCuerpo(v.repisa + 0.55f, fx) else 1f
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
