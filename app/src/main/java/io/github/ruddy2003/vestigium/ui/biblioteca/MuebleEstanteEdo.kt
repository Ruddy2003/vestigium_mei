package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * La paleta del cuarto de la casa de té de la posta (Japón Edo, hacia 1830): el sugi ahumado de la pared, el hinoki del
 * estante, el papel viejo del forro, el washi, la tinta, el bermellón de los sellos, el añil y el verde pino de las telas, la
 * paja, el cobre; y las luces del cuarto.
 */
internal object EdoTinta {
    /** Las tablas de sugi de la pared, ahumadas por el fogón de la casa, y el hollín que sube hasta el techo. */
    val SUGI = Color(0xFF86684F)
    val SUGI_OSC = Color(0xFF4C3A2F)
    val HOLLIN = Color(0xFF2C2421)
    /** El hinoki del estante: su luz, su sombra y su veta. */
    val HINOKI = Color(0xFFD8B884)
    val HINOKI_OSC = Color(0xFF9C7649)
    val VETA = Color(0xFF85633D)
    /** La madera oscura del nageshi, del pilar y del mástil del shamisen. */
    val KURI = Color(0xFF4A2F24)
    /** El papel viejo con que se forró el fondo del estante, el washi nuevo y la tinta. */
    val KAMI = Color(0xFFCDBB92)
    val WASHI = Color(0xFFF2E9D1)
    val SUMI = Color(0xFF221D1E)
    /** El bermellón de los sellos, el añil y el verde pino de las telas, la paja, el cobre y la tela sin teñir. */
    val SHU = Color(0xFFB43C2C)
    val AI = Color(0xFF27425F)
    val MATSU = Color(0xFF4E6B4D)
    val WARA = Color(0xFFCDB06E)
    val COBRE = Color(0xFFB36F3D)
    val KINARI = Color(0xFFCFC3A5)

    // Las luces: la noche del cuarto, la de los Sueños, el anochecer de los Sueños en claro, la vela del farol, la luna y el sol.
    val NOCHE = Color(0xFF151A2C)
    val NOCHE_SUENOS = Color(0xFF0E1736)
    val ATARDECER = Color(0xFF5B4572)
    val LLAMA = Color(0xFFFFD28C)
    val LUNA = Color(0xFFC2D1F2)
    val SOL = Color(0xFFFFF2D2)
    val OCASO = Color(0xFFFFB27E)
}

/**
 * El aire del momento en el cuarto: de día (la luz del ranma de papel, arriba a la izquierda), de noche (el modo oscuro: el
 * farol de viaje colgado a la derecha) y en los Sueños ([noche]: el anochecer en claro, con el color de cada cosa; la noche
 * honda en oscuro, [lunar], con la luna en el papel del ranma). [cerca] es cuánto llega del farol (0 a 1) y [frio], de la luna.
 */
internal class AireEdo(val oscuro: Boolean, val noche: Boolean) {
    /** Si arde el farol. */
    val lumbre: Boolean get() = oscuro || noche
    /** El color en que se hunde lo que no se alumbra. */
    val hondo: Color get() = if (oscuro) (if (noche) EdoTinta.NOCHE_SUENOS else EdoTinta.NOCHE) else SOMBRA_MUEBLE
    /** Los Sueños de noche: el farol hace un charco más corto y la luna entra con más fuerza por el ranma. */
    val lunar: Boolean get() = oscuro && noche
    private val luna: Float get() = if (lunar) 1.35f else 1f

    private fun lunada(c: Color, f: Float): Color =
        if (f <= 0f) c else Color(c.red * (1f - 0.3f * f), c.green * (1f - 0.12f * f), (c.blue * (1f + 0.14f * f)).coerceAtMost(1f), c.alpha)

    fun luz(c: Color, cerca: Float, frio: Float = 0f): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(mezcla(c, EdoTinta.ATARDECER, 0.2f), FILO_MUEBLE, 0.14f * k) else c
        val f = (frio * luna).coerceIn(0f, 1f) * (1f - k)
        if (lunar) return lunada(mezcla(mezcla(c, hondo, 0.9f - 0.8f * k.pow(0.95f) - 0.22f * f), FILO_MUEBLE, 0.3f * k), f)
        val base = mezcla(c, hondo, 0.87f - 0.77f * k.pow(0.95f) - 0.18f * f)
        return mezcla(mezcla(base, FILO_MUEBLE, 0.2f * k), EdoTinta.LUNA, 0.12f * f)
    }
    fun sombra(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(if (noche) a * 1.15f else a)
    fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.15f else if (noche) a * 0.55f else a)
    fun filo(a: Float): Color = FILO_MUEBLE.copy(alpha = if (oscuro) a.coerceIn(0f, 1f) else if (noche) (a * 0.4f).coerceIn(0f, 1f) else 0f)
    fun helado(a: Float): Color = EdoTinta.LUNA.copy(alpha = if (oscuro) (a * luna).coerceIn(0f, 1f) else 0f)

    /** El color del ánimo: de noche se apaga menos que lo demás, para que se siga adivinando en la repisa más lejana. */
    fun animo(t: Color, cerca: Float, frio: Float = 0f): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(mezcla(t, EdoTinta.ATARDECER, 0.07f), FILO_MUEBLE, 0.05f * k) else t
        val f = (frio * luna).coerceIn(0f, 1f) * (1f - k)
        if (lunar) {
            val d = 0.56f - 0.56f * k
            val s = 1f - 0.72f * d
            return lunada(mezcla(mezcla(Color(t.red * s, t.green * s, t.blue * s), hondo, 0.4f * d), FILO_MUEBLE, 0.3f * k), f * 0.7f)
        }
        return mezcla(mezcla(mezcla(t, hondo, 0.58f - 0.48f * k - 0.14f * f), FILO_MUEBLE, 0.1f * k), EdoTinta.LUNA, 0.1f * f)
    }
    /** El papel donde va el día: casi no se apaga, para que el número se lea siempre. */
    fun etiqueta(cerca: Float, base: Color = EdoTinta.WASHI): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(base, EdoTinta.ATARDECER, 0.08f) else base
        if (lunar) return mezcla(mezcla(base, hondo, 0.46f - 0.42f * k), FILO_MUEBLE, 0.22f * k)
        return mezcla(mezcla(base, hondo, 0.4f - 0.32f * k), FILO_MUEBLE, 0.14f * k)
    }
    val tinta: Color get() = if (oscuro) mezcla(EdoTinta.SUMI, hondo, 0.4f) else EdoTinta.SUMI
}

/**
 * El dibujo del estante del diario de Japón Edo (lo usa `EstanteEdo`, en `BibliotecaModerna.kt`): el cuarto de atrás de una
 * casa de té en una posta del camino real, hacia 1830, donde se guardan las cuentas, las cartas que dejan los viajeros y los
 * cuadernos que presta el librero ambulante. Es un estante abierto de hinoki, armado como lo arma un carpintero: dos postes y
 * tablas pasantes (nuki) que salen por fuera de los postes con su cuña de madera; el fondo, una tabla forrada con papel viejo
 * reaprovechado. Detrás, la pared de tablas de sugi ahumadas por el fogón, el nageshi (la viga) de lado a lado y, arriba a la
 * izquierda, el ranma de papel por donde entra la luz del cuarto de al lado; a la derecha, el pilar cortado por el borde. Encima
 * del estante, el shamisen que dejó un huésped, el ábaco de las cuentas, las sandalias de paja colgadas de un clavo, los cojines
 * de los huéspedes y, del nageshi, el farol de viaje plegable, que de noche es la luz del cuarto. Al pie, el suelo de tablas, la
 * cubeta del arroz con sus aros de cobre y la escoba de la mañana. El muro y el mueble están en `MuebleEstanteEdoMuro.kt`, lo
 * de arriba y lo de los huecos en `MuebleEstanteEdoArriba.kt` y los volúmenes en `MuebleEstanteEdoVolumenes.kt`.
 */
internal object MuebleEstanteEdo {
    val LETRA: FontFamily = LetraExtra.shippori

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.56f
    const val BASE = 0.27f
    const val LADO = 0.075f
    val ALTO_REPISA: Dp = 108.dp
    val TABLA: Dp = 15.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (27 + (palabras / 36).coerceAtMost(18)).dp

    /** Cuál de los tres es una entrada: 0, el estuche de tela (chitsu); 1, la pila de cuadernos cosidos; 2, la bolsa de tela. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla, 93)
        return if (x < 0.4f) 0 else if (x < 0.7f) 1 else 2
    }

    /** El estuche, el más alto; la bolsa, mediana; la pila, la más baja (y más alta cuanto más escribiste). */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.87f + 0.07f * azar(semilla, 1)
        1 -> 0.56f + 0.12f * (palabras / 800f).coerceIn(0f, 1f) + 0.04f * azar(semilla, 1)
        else -> 0.74f + 0.06f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde el farol (el centro de su papel), en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val LAMP_X = 0.865f
    const val LAMP_Y = 0.245f
    /** El centro de la luz del ranma (de día el sol del cuarto de al lado; en los Sueños de noche, la luna). */
    const val RANMA_X = 0.2f
    const val RANMA_Y = 0.06f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 123f
    /** En la base: dónde empieza el suelo, en fracción de su alto. */
    const val SUELO = 0.4f

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireEdo(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val b = d.base
        val suelo = b.top + b.height * SUELO
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * u
        val farol = Offset(w * LAMP_X, w * LAMP_Y)
        val g = MarcoEdo(w, h, ch, d.izquierda.right, d.derecha.left, if (n > 0) d.tablas.last().bottom else ch, suelo, fila, n)
        val cerca: (Offset) -> Float = { p ->
            if (p.y <= ch) alcanceEdo((p.x - farol.x) / w, (p.y - farol.y) / w, n, a.lunar) else cercaCuerpoEdo((p.y - ch) / fila, n, p.x / w, a.lunar)
        }
        val frio: (Offset) -> Float = { p ->
            if (p.y <= ch) frioEdo(p.x / w - RANMA_X, p.y / w - RANMA_Y) else frioCuerpoEdo((p.y - ch) / fila, n, p.x / w)
        }

        // ---- la pared, el nageshi, el pilar y el ranma; la luz que entra por él; el charco del farol
        paredEdo(a, g)
        luzRanmaEdo(a, g)
        if (a.lunar) {
            mancha(farol, w * 0.7f, FILO_MUEBLE.copy(alpha = 0.2f))
            mancha(farol, w * 0.34f, FILO_MUEBLE.copy(alpha = 0.34f))
            mancha(farol, w * 0.14f, EdoTinta.LLAMA.copy(alpha = 0.5f))
        } else if (a.lumbre) {
            val f = if (oscuro) 1f else 0.6f
            mancha(farol, w * 1.15f, FILO_MUEBLE.copy(alpha = 0.18f * f))
            mancha(farol, w * 0.5f, FILO_MUEBLE.copy(alpha = 0.3f * f))
            mancha(farol, w * 0.2f, EdoTinta.LLAMA.copy(alpha = 0.42f * f))
        }
        // ---- el suelo de tablas
        sueloEdo(a, g, cerca)

        // ---- el estante: el fondo forrado de papel, las tablas pasantes, los postes, la tabla de arriba y el pie
        d.repisas.forEachIndexed { k, r -> fondoHuecoEdo(k, r, d.ocupado.getOrNull(k) ?: r.left, a, g, cerca, frio) }
        d.tablas.forEachIndexed { k, r -> tablaPasanteEdo(k, r, a, g, cerca, frio) }
        postesEdo(a, g, d)
        tapaEstanteEdo(a, g, cerca, frio)
        pieEstanteEdo(a, g, cerca)

        // ---- lo que hay en los huecos libres
        d.repisas.forEachIndexed { k, r ->
            val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
            val c = Offset((oc + r.right) / 2, r.center.y)
            huecoLibreEdo(k, r, oc, a, cerca(c), frio(c))
        }
        // ---- el rótulo de cada mes: una tablilla de hinoki clavada en el canto
        d.placas.forEach { p -> rotuloEdo(p, medidor, a, cerca(p.r.center)) }

        // ---- lo de arriba, de atrás adelante
        val apoyo = g.apoyo
        sandaliasEdo(Offset(w * 0.075f, w * 0.135f), w, a, cerca(Offset(w * 0.075f, w * 0.22f)), frio(Offset(w * 0.075f, w * 0.22f)))
        val pieSham = Offset(w * 0.255f, apoyo)
        shamisenEdo(pieSham, w, a, cerca(pieSham - Offset(0f, w * 0.15f)), frio(pieSham - Offset(0f, w * 0.15f)))
        val pieSoro = Offset(w * 0.555f, apoyo)
        sorobanEdo(pieSoro, w, a, cerca(pieSoro - Offset(0f, w * 0.05f)), frio(pieSoro - Offset(0f, w * 0.05f)))
        val pieCoj = Offset(w * 0.8f, apoyo)
        cojinesEdo(pieCoj, w, a, cerca(pieCoj - Offset(0f, w * 0.04f)), frio(pieCoj))
        plectroEdo(Offset(w * 0.39f, apoyo + w * 0.004f), w, a, cerca(Offset(w * 0.39f, apoyo)))
        farolEdo(farol, w, a, g)

        // ---- al pie: la cubeta del arroz y la escoba apoyada en el poste
        val pieCubeta = Offset(w * 0.16f, g.suelo + w * 0.105f)
        cubetaArrozEdo(pieCubeta, w, a, maxOf(cerca(pieCubeta - Offset(0f, w * 0.06f)), if (a.lunar) 0.06f else 0.1f), frio(pieCubeta))
        escobaEdo(g, a, cerca(Offset(g.x1, g.fondo - fila * 0.6f)))

        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) = volumenEdo(v, medidor, oscuro)
}

/**
 * Las medidas del cuarto que comparten las piezas: el ancho [w] y el alto [h] del lienzo, el pie de la corona [ch], el borde de
 * dentro de los postes ([x0], [x1]), el pie de la última tabla ([fondo]), dónde empieza el suelo ([suelo]), una repisa con su
 * tabla ([fila]) y cuántas repisas hay ([n]).
 */
internal class MarcoEdo(val w: Float, val h: Float, val ch: Float, val x0: Float, val x1: Float, val fondo: Float, val suelo: Float, val fila: Float, val n: Int) {
    /** El canto de la tabla de arriba y lo que se ve de su cara. */
    val gTapa: Float get() = w * 0.036f
    val fTapa: Float get() = w * 0.014f
    /** Donde se apoya lo de arriba (en la cara de la tabla de arriba, un poco hacia atrás). */
    val apoyo: Float get() = ch - gTapa - fTapa * 0.4f
    /** El nageshi: la viga de lado a lado, arriba. */
    val nageshiArriba: Float get() = w * 0.074f
    val nageshiAbajo: Float get() = w * 0.104f
    /** Lo que sale cada tabla por fuera de los postes, y el poste mismo. */
    val salida: Float get() = w * 0.03f
    val posteIzq: Float get() = salida
    val posteDer: Float get() = w - salida
}

// ---------------------------------------------------------------- la luz, compartida por el mueble y los volúmenes

/** La luz del farol que llega a un punto a [dx] y [dy] de él, en anchos del mueble; con [corto] (los Sueños de noche), menos. */
internal fun alcanceEdo(dx: Float, dy: Float, repisas: Int, corto: Boolean = false): Float {
    val radio = 1.0f + 0.1f * repisas.coerceIn(2, 5)
    return (1f - sqrt(dx * dx + dy * dy) / (if (corto) radio * 0.8f else radio)).coerceIn(0f, 1f)
}

/** La luz del farol en el cuerpo del mueble: [pos] cuenta repisas desde la de arriba. */
internal fun cercaCuerpoEdo(pos: Float, repisas: Int, fx: Float, corto: Boolean = false): Float {
    val y = MuebleEstanteEdo.CORONA + pos * MuebleEstanteEdo.FILA / MuebleEstanteEdo.ANCHO_NOMINAL
    return alcanceEdo(fx - MuebleEstanteEdo.LAMP_X, y - MuebleEstanteEdo.LAMP_Y, repisas, corto)
}

/** Cuánta luna entra por el ranma hasta un punto a [dx] y [dy] del centro de su luz, en anchos del mueble. */
internal fun frioEdo(dx: Float, dy: Float): Float = ((1f - sqrt(dx * dx * 0.7f + dy * dy) / 1.0f) * 0.85f).coerceIn(0f, 1f)

/** La luna en el cuerpo del mueble, con la misma cuenta de repisas que [cercaCuerpoEdo]. */
internal fun frioCuerpoEdo(pos: Float, repisas: Int, fx: Float): Float =
    frioEdo(fx - MuebleEstanteEdo.RANMA_X, MuebleEstanteEdo.CORONA - MuebleEstanteEdo.RANMA_Y + pos * MuebleEstanteEdo.FILA / MuebleEstanteEdo.ANCHO_NOMINAL)

// ---------------------------------------------------------------- ayudantes de forma

internal fun corridoEdo(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

/** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
internal fun bordeEdo(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corridoEdo(p, dx, dy), PathOperation.Difference) }

internal fun poligonoEdo(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}
