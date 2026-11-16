package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * El dibujo del estante del diario de Helénico (lo usa `EstanteHeleno`, en `BibliotecaAntiguedad.kt`): el rincón del maestro
 * de letras en una casa ática del siglo V a. C. El muro es de revoque pintado por zonas, en otra disposición que el del
 * Ideario: un friso blanco hueso con el meandro pintado en rojo, ya gastado; un filete rojo y otro azul; el paño grande azul
 * apagado; abajo, la franja roja y el zócalo oscuro sobre el suelo de tierra. Contra él, un armazón de madera vieja: un
 * tablero ancho que vuela sobre dos postes, tablas gruesas encajadas y clavijadas en ellos y, al pie, una laja bajo cada
 * poste; no tiene fondo, así que detrás de las tablillas se ve el muro en sombra. Todo el dibujo sale de [estante] y de
 * [volumen]; la luz de la noche de los dos sale de la misma curva, [alcance].
 */
internal object MuebleEstanteHeleno {
    // ---------------------------------------------------------------- la paleta (sale del cuadro de Helénico)
    /** El revoque encalado del friso y del nicho; también el hueso de las etiquetas y la lana del manto. */
    private val HUESO = Color(0xFFE9DFC8)
    /** El paño grande del muro, azul apagado; el filete azul del Egeo; la franja roja y el zócalo oscuro. */
    private val PANO = Color(0xFF7A9597)
    private val EGEO = Color(0xFF2E5E8E)
    private val ROJO = Color(0xFF9A3B2C)
    private val ZOCALO = Color(0xFF3F3338)
    /** La madera vieja del armazón y su sombra honda. */
    private val MADERA = Color(0xFF96703F)
    private val MADERA_OSC = Color(0xFF523723)
    /** El boj claro de las tablillas, la cera oscura y lo inciso en ella (los de la tapa). */
    private val BOJ = Color(0xFFD8BA80)
    private val CERA = Color(0xFF2B2420)
    private val INCISO = Color(0xFFD9C9A6)
    /** El papiro, la arcilla cocida, el barniz negro y el bronce. */
    private val PAPIRO = Color(0xFFDDC790)
    private val TERRACOTA = Color(0xFFC2652F)
    private val BARNIZ = Color(0xFF1A1410)
    private val BRONCE = Color(0xFFB07A3A)
    /** El cuero de las correas, el mimbre, el olivo fresco y la tierra apisonada. */
    private val CUERO = Color(0xFF6E4527)
    private val MIMBRE = Color(0xFFB8934E)
    private val OLIVO = Color(0xFF6E8A4C)
    private val TIERRA = Color(0xFFBDA27A)

    // Las luces: la noche del cuarto, la de los Sueños (más azul), el anochecer de los Sueños en claro, la llama y la luna.
    private val NOCHE = Color(0xFF17142A)
    private val NOCHE_SUENOS = Color(0xFF111A36)
    private val ANOCHECER = Color(0xFF2B315C)
    private val LLAMA = Color(0xFFFFD58A)
    private val LUNA = Color(0xFFEDE6C8)
    private val OJO = Color(0xFFF2C94A)

    private val LETRA: FontFamily = Letra.garamond

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.52f
    const val BASE = 0.24f
    const val LADO = 0.07f
    val ALTO_REPISA: Dp = 108.dp
    val TABLA: Dp = 16.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (24 + (palabras / 40).coerceAtMost(18)).dp

    /** Cuál de los tres es una entrada: 0, el pinax (díptico de boj); 1, el políptico atado con correas; 2, el rollo en su funda. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod(semilla xor (semilla ushr 7), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** El pinax, mediano; el políptico, más bajo; el rollo, el más alto. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.76f + 0.09f * azar(semilla, 1)
        1 -> 0.6f + 0.09f * azar(semilla, 1)
        else -> 0.88f + 0.08f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde la lucerna (el medio entre sus dos llamas), en anchos del mueble desde la esquina de arriba a la izquierda. */
    private const val LAMP_X = 0.149f
    private const val LAMP_Y = 0.27f
    /** Dónde pisa la lucerna en el alféizar de su nicho, en anchos del mueble. */
    private const val LUCERNA_Y = 0.306f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    private const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    private const val FILA = 124f
    /** En la base: dónde empieza el suelo, en fracción de su alto. */
    private const val SUELO = 0.62f
    /** Hasta dónde llegan los postes hacia dentro, en fracción del ancho. */
    private const val POSTE = 0.058f

    /**
     * La curva de la luz de la lucerna, la misma para el mueble y para los volúmenes: cuánta llega (de 1 a 0) a un punto que
     * está a [dx] y [dy] de la llama, en anchos del mueble. En un estante corto la luz se acaba antes, para que la última
     * repisa quede siempre en penumbra; de cinco repisas en adelante el alcance ya no crece.
     */
    fun alcance(dx: Float, dy: Float, repisas: Int): Float {
        val radio = 1.05f + 0.1f * repisas.coerceIn(2, 5)
        return (1f - sqrt(dx * dx + dy * dy) / radio).coerceIn(0f, 1f)
    }

    /** La luz que llega al cuerpo del mueble: [pos] cuenta repisas desde la de arriba (1.5 es media altura de la segunda). */
    fun cercaCuerpo(pos: Float, repisas: Int, fx: Float): Float =
        alcance(fx - LAMP_X, CORONA - LAMP_Y + pos * FILA / ANCHO_NOMINAL, repisas)

    /**
     * El aire del momento: de día, de noche (el modo oscuro) y en los Sueños ([noche]: al anochecer en claro, la noche más
     * azul en oscuro). De él salen el color de cada cosa según cuánta lucerna le llega, la sombra, la luz y el filo.
     */
    private class Aire(val oscuro: Boolean, val noche: Boolean) {
        /** Si arde la lucerna. */
        val lumbre: Boolean get() = oscuro || noche
        /** El color en que se hunde lo que no se alumbra. */
        val hondo: Color get() = if (oscuro) (if (noche) NOCHE_SUENOS else NOCHE) else SOMBRA_MUEBLE

        fun luz(c: Color, cerca: Float): Color {
            val k = cerca.coerceIn(0f, 1f)
            if (!oscuro) return if (noche) mezcla(mezcla(c, ANOCHECER, 0.4f - 0.18f * k), FILO_MUEBLE, 0.12f * k) else c
            return mezcla(mezcla(c, hondo, 0.88f - 0.78f * k.pow(0.95f)), FILO_MUEBLE, 0.2f * k)
        }
        fun sombra(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)
        fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.15f else if (noche) a * 0.55f else a)
        fun filo(a: Float): Color = FILO_MUEBLE.copy(alpha = if (oscuro) a.coerceIn(0f, 1f) else if (noche) (a * 0.4f).coerceIn(0f, 1f) else 0f)
        /** El color del ánimo: de noche se apaga menos que lo demás, para que se siga viendo en la repisa más lejana. */
        fun animo(t: Color, cerca: Float): Color {
            val k = cerca.coerceIn(0f, 1f)
            if (!oscuro) return if (noche) mezcla(t, ANOCHECER, 0.2f) else t
            return mezcla(mezcla(t, hondo, 0.62f - 0.52f * k), FILO_MUEBLE, 0.1f * k)
        }
        /** La etiqueta clara donde va el día: casi no se apaga, para que el número se lea siempre. */
        fun etiqueta(cerca: Float): Color {
            val k = cerca.coerceIn(0f, 1f)
            if (!oscuro) return if (noche) mezcla(HUESO, ANOCHECER, 0.12f) else mezcla(HUESO, Color.White, 0.25f)
            return mezcla(mezcla(HUESO, hondo, 0.4f - 0.32f * k), FILO_MUEBLE, 0.16f * k)
        }
        val tinta: Color get() = if (oscuro) mezcla(BARNIZ, hondo, 0.5f) else BARNIZ
        /** Lo inciso en la cera: la cera clara de abajo. */
        fun inciso(cerca: Float): Color = if (oscuro) mezcla(INCISO, hondo, 0.28f - 0.24f * cerca.coerceIn(0f, 1f)) else if (noche) mezcla(INCISO, ANOCHECER, 0.1f) else INCISO
    }

    // ---------------------------------------------------------------- ayudantes

    private fun corrido(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

    /** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
    private fun borde(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corrido(p, dx, dy), PathOperation.Difference) }

    private fun ovalo(c: Offset, rx: Float, ry: Float) = Path().apply { addOval(Rect(c.x - rx, c.y - ry, c.x + rx, c.y + ry)) }

    private fun poligono(vararg xy: Float) = Path().apply {
        moveTo(xy[0], xy[1])
        for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
        close()
    }

    /** Une [q] con curvas suaves (Catmull-Rom pasado a Bézier). */
    private fun suave(p: Path, q: List<Offset>) {
        for (i in 0 until q.size - 1) {
            val a = q[maxOf(i - 1, 0)]; val b = q[i]; val c = q[i + 1]; val d = q[minOf(i + 2, q.size - 1)]
            val c1 = b + (c - a) / 6f; val c2 = c - (d - b) / 6f
            p.cubicTo(c1.x, c1.y, c2.x, c2.y, c.x, c.y)
        }
    }

    /** El contorno de algo torneado: [p] son pares (medio ancho, altura) de arriba abajo, en unidades de [s] desde [cx] y [y0]. */
    private fun perfil(cx: Float, y0: Float, s: Float, p: FloatArray): Path {
        val der = List(p.size / 2) { Offset(cx + p[2 * it] * s, y0 + p[2 * it + 1] * s) }
        val izq = der.reversed().map { Offset(2 * cx - it.x, it.y) }
        return Path().apply { moveTo(der[0].x, der[0].y); suave(this, der); lineTo(izq[0].x, izq[0].y); suave(this, izq); close() }
    }

    /** Un punto de la curva de Bézier que va de [a] a [d]. */
    private fun bezier(a: Offset, b: Offset, c: Offset, d: Offset, t: Float): Offset {
        val m = 1f - t
        return a * (m * m * m) + b * (3 * m * m * t) + c * (3 * m * t * t) + d * (t * t * t)
    }

    /**
     * El volumen de gouache sobre algo redondo ya pintado: la media luna de sombra del lado contrario a la luz (en dos tonos),
     * el claro del otro y, con [brillo], el del barniz. De día la luz viene de la izquierda; de noche, de donde esté la lucerna
     * ([haciaX]: 1 a la derecha, -1 a la izquierda), con un filo cálido tanto más fuerte cuanto más [cerca].
     */
    private fun DrawScope.redondo(forma: Path, a: Aire, cerca: Float, haciaX: Float, brillo: Float = 0f, fuerza: Float = 1f, claro: Float = 1f) {
        val r = forma.getBounds(); val an = r.width; val hh = r.height
        val sl = if (a.oscuro) haciaX else -1f
        drawPath(borde(forma, sl * an * 0.26f, -hh * 0.02f), a.sombra(fuerza * 0.2f))
        drawPath(borde(forma, sl * an * 0.11f, -hh * 0.01f), a.sombra(fuerza * 0.2f))
        clipPath(forma) {
            drawOval(if (a.oscuro) FILO_MUEBLE.copy(alpha = 0.26f * cerca * claro) else a.claro(0.2f * claro), Offset(if (sl < 0) r.left + an * 0.1f else r.right - an * 0.36f, r.top + hh * 0.06f), Size(an * 0.26f, hh * 0.72f))
            if (brillo > 0f) drawRoundRect(if (a.oscuro) FILO_MUEBLE.copy(alpha = (brillo * 1.6f * cerca).coerceAtMost(0.7f)) else a.claro(brillo),
                Offset(if (sl < 0) r.left + an * 0.21f else r.right - an * 0.255f, r.top + hh * 0.14f), Size(an * 0.045f, hh * 0.44f), CornerRadius(an * 0.03f))
        }
        if (a.oscuro && cerca > 0.03f) drawPath(borde(forma, -sl * an * 0.04f, hh * 0.03f), FILO_MUEBLE.copy(alpha = (0.05f + 0.6f * cerca).coerceAtMost(0.65f)))
    }

    /** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Aire, fuerza: Float = 0.34f) =
        drawOval(a.sombra(if (a.oscuro) fuerza * 1.3f else fuerza), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    /** Una clavija de madera clavada de frente, con su sombra y la luz en la cabeza. */
    private fun DrawScope.clavija(p: Offset, r: Float, color: Color, a: Aire, filo: Float = 0f) {
        drawCircle(a.sombra(0.4f), r * 1.1f, p + Offset(r * 0.35f, r * 0.45f))
        drawCircle(color, r, p)
        drawCircle(a.claro(0.45f), r * 0.42f, p - Offset(r * 0.32f, r * 0.32f))
        if (a.oscuro && filo > 0.05f) drawArc(FILO_MUEBLE.copy(alpha = 0.6f * filo), 160f, 110f, false, Offset(p.x - r, p.y - r), Size(2 * r, 2 * r), style = Stroke(r * 0.35f))
    }

    /** Una greca (meandro) pintada a pincel a lo largo de [r]. */
    private fun DrawScope.greca(r: Rect, color: Color) {
        val g = r.height; var x = r.left
        val p = Path().apply { moveTo(x, r.bottom) }
        while (x < r.right) {
            p.lineTo(x, r.top); p.lineTo(x + g * 0.8f, r.top); p.lineTo(x + g * 0.8f, r.top + g * 0.62f); p.lineTo(x + g * 0.4f, r.top + g * 0.62f)
            p.lineTo(x + g * 0.4f, r.top + g * 0.32f); p.moveTo(x + g * 0.4f, r.bottom); p.lineTo(x + g * 1.2f, r.bottom); x += g * 1.2f
        }
        drawPath(p, color, style = Stroke(maxOf(1f * density, g * 0.13f)))
    }

    /** Un tiesto (óstrakon) con volumen: arcilla, a veces con su cara de barniz, el canto de arriba a la luz. */
    private fun DrawScope.tiesto(c: Offset, r: Float, semilla: Int, a: Aire, cerca: Float) {
        val p = Path()
        val lados = 4 + Math.floorMod(semilla, 3)
        for (k in 0 until lados) {
            val ang = 6.283f * k / lados + azar(semilla, k) * 0.8f
            val rr = r * (0.7f + 0.4f * azar(semilla, k + 9))
            val o = Offset(c.x + rr * cos(ang), c.y + rr * 0.66f * sin(ang))
            if (k == 0) p.moveTo(o.x, o.y) else p.lineTo(o.x, o.y)
        }
        p.close()
        drawPath(corrido(p, r * 0.14f, r * 0.2f), a.sombra(0.32f))
        drawPath(p, a.luz(mezcla(TERRACOTA, PAPIRO, 0.5f * azar(semilla, 20)), cerca))
        if (azar(semilla, 21) < 0.45f) clipPath(p) {
            drawRect(a.luz(BARNIZ, cerca), Offset(c.x - r * 1.3f, c.y - r * 1.3f), Size(r * 2.6f, r * (0.7f + 0.7f * azar(semilla, 22))))
        }
        drawPath(borde(p, r * 0.16f, r * 0.16f), a.claro(0.4f))
        drawPath(p, a.sombra(0.35f), style = Stroke(0.5f * density))
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = Aire(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val b = d.base
        val suelo = b.top + b.height * SUELO
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * u
        val llama = Offset(w * LAMP_X, w * LAMP_Y)
        // La luz en un punto del lienzo: arriba, por la distancia a la llama; en el cuerpo, la misma cuenta que hacen los volúmenes.
        fun cerca(p: Offset): Float =
            if (p.y <= ch) alcance((p.x - llama.x) / w, (p.y - llama.y) / w, n) else cercaCuerpo((p.y - ch) / fila, n, p.x / w)
        fun hacia(x: Float) = if (llama.x >= x) 1f else -1f
        /** Hacia dónde cae la sombra de algo en [p] sobre la pared: de día, a la derecha y abajo; de noche, lejos de la llama. */
        fun caida(p: Offset, largo: Float): Offset {
            if (!oscuro) return Offset(largo * 1.15f, largo * 0.8f)
            val v = p - llama; val dd = v.getDistance().coerceAtLeast(1f)
            return v / dd * (largo * 3.2f)
        }
        /** La sombra en la pared de lo que está de pie sobre el tablero: no baja de donde pisa (ahí la taparía el propio tablero). */
        fun dePie(p: Offset, largo: Float): Offset = caida(p, largo).let { Offset(it.x, minOf(it.y, largo * 0.4f)) }
        val gT = w * 0.034f            // el canto del tablero de arriba
        val fT = w * 0.014f            // lo que se ve de su cara de arriba
        val apoyo = ch - gT - fT * 0.5f
        val x0 = w * POSTE; val x1 = w * (1f - POSTE)

        muro(d, a, suelo, ch)
        nicho(a, llama)
        // El halo de la lucerna: unas pocas manchas, no un velo.
        if (a.lumbre) {
            val f = if (oscuro) 1f else 0.5f
            mancha(llama, w * 1.25f, FILO_MUEBLE.copy(alpha = 0.17f * f))
            mancha(llama, w * 0.6f, FILO_MUEBLE.copy(alpha = 0.28f * f))
            mancha(llama, w * 0.26f, LLAMA.copy(alpha = 0.34f * f))
        }

        // ---- el suelo de tierra apisonada, con sus guijarros
        drawRect(a.luz(TIERRA, 0.05f), Offset(0f, suelo), Size(w, h - suelo))
        pinceladas(Rect(0f, suelo, w, h), a.luz(mezcla(TIERRA, SOMBRA_MUEBLE, 0.4f), 0.05f), 741, 24, u, 0.12f)
        pinceladas(Rect(0f, suelo, w, h), a.luz(mezcla(TIERRA, LUZ_MUEBLE, 0.5f), 0.05f), 742, 16, u, 0.12f)
        for (k in 0 until 22) {
            val p = Offset(w * azar(743, k), suelo + (h - suelo) * (0.2f + 0.75f * azar(744, k)))
            val r = (0.8f + 1.8f * azar(745, k)) * u
            drawOval(a.sombra(0.3f), Offset(p.x - r + 0.6f * u, p.y - r * 0.4f), Size(2 * r, 1.1f * r))
            drawOval(a.luz(mezcla(TIERRA, if (k % 3 == 0) ZOCALO else HUESO, 0.4f), 0.05f), Offset(p.x - r, p.y - r * 0.6f), Size(2 * r, 1.1f * r))
        }
        drawRect(a.sombra(0.42f), Offset(0f, suelo), Size(w, 1.5f * u))
        drawRect(a.sombra(0.14f), Offset(0f, suelo + 1.5f * u), Size(w, 4f * u))

        // ---- las sombras del mueble en el muro y en el suelo
        // Bajo el mueble, entre las patas: el muro en penumbra y la sombra tendida en la tierra, corrida a la derecha.
        drawRect(a.sombra(if (oscuro) 0.5f else 0.34f), Offset(x0, b.top), Size(x1 - x0 + w * 0.02f, suelo - b.top))
        drawRect(a.sombra(0.16f), Offset(x0, b.top), Size(x1 - x0 + w * 0.02f, (suelo - b.top) * 0.4f))
        drawPath(poligono(x0 + w * 0.01f, suelo, w, suelo, w, suelo + w * 0.038f, x0 + w * 0.05f, suelo + w * 0.038f), a.sombra(if (oscuro) 0.42f else 0.26f))
        // La del poste de la derecha, que cae fuera del mueble.
        drawRect(a.sombra(if (oscuro) 0.4f else 0.22f), Offset(w * 0.99f, ch + w * 0.01f), Size(w * 0.01f, suelo - ch))

        // ---- el fondo de cada repisa: el muro en sombra, con la luz que entra por la izquierda
        d.repisas.forEachIndexed { k, r ->
            clipRect(x0, r.top, x1, r.bottom) {
                val an = x1 - x0
                drawRect(a.sombra(if (oscuro) 0.3f else 0.33f), Offset(x0, r.top), Size(an, r.height))
                // La sombra de la tabla de arriba: honda junto a ella y deshecha hacia abajo.
                val f = if (oscuro) 1.2f else 1f
                drawRect(a.sombra(0.34f * f), Offset(x0, r.top), Size(an, 8f * u))
                drawRect(a.sombra(0.2f * f), Offset(x0, r.top), Size(an, 17f * u))
                drawRect(a.sombra(0.11f * f), Offset(x0, r.top), Size(an, 30f * u))
                pinceladas(Rect(x0, r.top + 24f * u, x1, r.top + 40f * u), a.sombra(1f), 751 + k, 12, u, 0.09f)
                // La sombra del poste de la izquierda sobre el fondo, y el claro que deja pasar a su lado.
                drawRect(a.sombra(0.26f), Offset(x0, r.top), Size(8f * u, r.height))
                if (!oscuro) {
                    pinceladas(Rect(x0 + 8f * u, r.top + r.height * 0.3f, x0 + an * 0.62f, r.bottom), luzMueble(1f), 755 + k, 16, u, if (noche) 0.04f else 0.075f)
                    pinceladas(Rect(x0 + 8f * u, r.top + r.height * 0.5f, x0 + an * 0.34f, r.bottom), luzMueble(1f), 759 + k, 10, u, if (noche) 0.04f else 0.075f)
                }
                // El costado derecho, más hondo.
                pinceladas(Rect(x1 - an * 0.34f, r.top, x1, r.bottom), a.sombra(1f), 763 + k, 22, u, 0.07f)
                pinceladas(Rect(x1 - an * 0.16f, r.top, x1, r.bottom), a.sombra(1f), 767 + k, 18, u, 0.09f)
                drawRect(a.sombra(0.12f), Offset(x1 - 11f * u, r.top), Size(11f * u, r.height))
                drawRect(a.sombra(0.2f), Offset(x1 - 5f * u, r.top), Size(5f * u, r.height))
                // La sombra que arroja el último volumen sobre el fondo, a su derecha.
                val oc = d.ocupado.getOrNull(k) ?: r.left
                if (oc > r.left + 6f * u) {
                    val largo = (if (oscuro) 22f else 13f) * u
                    drawPath(poligono(oc - 2f * u, r.bottom, oc - 2f * u, r.top + r.height * 0.2f, oc + largo, r.top + r.height * 0.3f, oc + largo, r.bottom), a.sombra(if (oscuro) 0.4f else 0.28f))
                    drawPath(poligono(oc + largo, r.bottom, oc + largo, r.top + r.height * 0.3f, oc + largo * 1.5f, r.top + r.height * 0.36f, oc + largo * 1.5f, r.bottom), a.sombra(0.14f))
                }
            }
        }

        // ---- las tablas, con su cara de arriba, y los postes
        d.tablas.forEachIndexed { k, r ->
            tablon(Rect(x0 - 1f * u, r.top, x1 + 1f * u, r.bottom), 3.5f * u, 770 + 7 * k, a) { x -> cerca(Offset(x, r.center.y)) }
        }
        // Las riostras: un taco en escuadra en cada rincón, que traba el poste con la tabla de encima.
        d.repisas.forEach { r ->
            riostra(Offset(x0, r.top), 1f, 13f * u, a, cerca(Offset(x0, r.top)))
            riostra(Offset(x1, r.top), -1f, 13f * u, a, cerca(Offset(x1, r.top)))
        }
        d.repisas.forEachIndexed { k, r ->
            val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
            huecoLibre(k, r, oc, a, cerca(Offset((oc + r.right) / 2, r.center.y)), hacia((oc + r.right) / 2))
        }
        listOf(Rect(w * 0.01f, ch, x0, suelo + 3f * u), Rect(x1, ch, w * 0.99f, suelo + 3f * u)).forEachIndexed { i, r ->
            // La laja que lo separa de la tierra húmeda.
            val laja = Rect(r.left - w * 0.012f, suelo + 1.5f * u, r.right + w * 0.014f, suelo + 7.5f * u)
            contacto(Offset(laja.center.x + 3f * u, laja.bottom), laja.width * 1.25f, 5f * u, a)
            drawRoundRect(a.luz(mezcla(HUESO, ZOCALO, 0.42f), cerca(laja.center)), laja.topLeft, laja.size, CornerRadius(2.4f * u))
            drawRoundRect(a.claro(0.35f), laja.topLeft, Size(laja.width, 1.6f * u), CornerRadius(1.2f * u))
            drawRect(a.sombra(0.3f), Offset(laja.left + 2f * u, laja.bottom - 1.6f * u), Size(laja.width - 4f * u, 1.6f * u))
            poste(r, 790 + i, a, n, ch, fila, hacia(r.center.x))
            d.tablas.forEach { t ->
                for (dy in listOf(0.3f, 0.72f)) {
                    val p = Offset(r.center.x + (if (dy < 0.5f) -1f else 1f) * r.width * 0.16f, t.top + t.height * dy)
                    clavija(p, 1.7f * u, a.luz(mezcla(MADERA, LUZ_MUEBLE, 0.22f), cerca(p)), a, cerca(p))
                }
            }
        }

        // ---- el rótulo de cada mes: un leukoma (la tablilla blanqueada de los avisos) colgado del canto con dos clavos
        d.placas.forEach { p ->
            val r = Rect(p.r.left, p.r.top - 1.5f * u, p.r.right, p.r.bottom + 1.5f * u)
            val k = cerca(r.center)
            val forma = poligono(r.left, r.top + 0.8f * u, r.right, r.top, r.right - 0.6f * u, r.bottom, r.left + 0.7f * u, r.bottom - 0.6f * u)
            drawPath(corrido(forma, 1.4f * u, 1.8f * u), a.sombra(0.4f))
            drawPath(forma, a.etiqueta(maxOf(k, 0.15f)))
            clipPath(forma) {
                pinceladas(r, a.sombra(1f), 761 + p.texto.length, 8, u, 0.06f)
                drawRect(a.sombra(0.14f), Offset(r.left, r.bottom - 2.2f * u), Size(r.width, 2.2f * u))
                drawRect(a.claro(0.5f), r.topLeft, Size(r.width, 1.2f * u))
            }
            val filete = if (oscuro) mezcla(ROJO, a.hondo, 0.25f) else ROJO
            drawLine(filete, Offset(r.left + 9f * u, r.top + 2.6f * u), Offset(r.right - 9f * u, r.top + 2.2f * u), 0.8f * u)
            drawLine(filete, Offset(r.left + 9f * u, r.bottom - 2.6f * u), Offset(r.right - 9f * u, r.bottom - 2.4f * u), 0.8f * u)
            listOf(r.left + 4.5f * u, r.right - 4.5f * u).forEach { x ->
                drawCircle(a.sombra(0.45f), 1.6f * u, Offset(x + 0.5f * u, r.center.y + 0.6f * u))
                drawCircle(a.luz(BRONCE, maxOf(k, 0.3f)), 1.4f * u, Offset(x, r.center.y))
                drawCircle(a.claro(0.5f), 0.5f * u, Offset(x - 0.4f * u, r.center.y - 0.4f * u))
            }
            textoEn(medidor, p.texto, Rect(r.left + 9f * u, r.top + 3f * u, r.right - 9f * u, r.bottom - 3f * u), LETRA, a.tinta, 10.5f, peso = FontWeight.Medium, espaciado = 1f, minimo = 5f)
        }

        // ---- el tablero de arriba, que vuela sobre los postes, y su sombra sobre ellos
        tablon(Rect(-2f * u, ch - gT, w + 2f * u, ch), fT, 765, a) { x -> cerca(Offset(x, ch - gT)) }
        if (a.lumbre) clipRect(0f, ch - gT - fT, w, ch) { mancha(Offset(llama.x, ch - gT - fT), w * 0.34f, FILO_MUEBLE.copy(alpha = if (oscuro) 0.42f else 0.2f)) }
        drawRect(a.sombra(if (oscuro) 0.5f else 0.36f), Offset(0f, ch), Size(w, 3.5f * u))
        drawRect(a.sombra(0.18f), Offset(0f, ch + 3.5f * u), Size(w, 5f * u))

        // ---- lo que cuelga del muro y lo que hay sobre el tablero, de atrás adelante
        val cEsc = Offset(w * 0.675f, apoyo - w * 0.172f)
        aspis(cEsc, w * 0.178f, apoyo, a, cerca(cEsc), hacia(cEsc.x), caida(cEsc, w * 0.02f))
        val clavo = Offset(w * 0.335f, w * 0.118f)
        fundaAulos(clavo, w * 0.215f, a, cerca(clavo + Offset(0f, w * 0.1f)), hacia(clavo.x), caida(clavo + Offset(0f, w * 0.1f), w * 0.013f))
        val clavo2 = Offset(w * 0.425f, w * 0.148f)
        saquito(clavo2, w * 0.05f, a, cerca(clavo2), hacia(clavo2.x), caida(clavo2, w * 0.012f))
        val pieRollos = Offset(w * 0.33f, apoyo)
        hazRollos(pieRollos, w * 0.064f, a, cerca(pieRollos - Offset(0f, w * 0.06f)), hacia(pieRollos.x), dePie(pieRollos, w * 0.012f))
        val pieLecito = Offset(w * 0.9f, apoyo)
        ramaOlivo(pieLecito - Offset(0f, w * 0.16f), w, a, cerca(pieLecito - Offset(0f, w * 0.25f)), caida(pieLecito - Offset(0f, w * 0.25f), w * 0.014f))
        val kl = cerca(pieLecito - Offset(0f, w * 0.08f))
        val formaLecito = lecitoForma(pieLecito, w * 0.165f)
        translate(dePie(pieLecito, w * 0.012f).x, dePie(pieLecito, w * 0.012f).y) { drawPath(formaLecito, a.sombra(if (oscuro) 0.4f else 0.2f)) }
        contacto(Offset(pieLecito.x + 2f * u, pieLecito.y), w * 0.075f, w * 0.011f, a)
        lecito(pieLecito, w * 0.165f, a, kl, hacia(pieLecito.x))
        val pieLechuza = Offset(w * 0.478f, apoyo + fT * 0.2f)
        lechuza(pieLechuza, w * 0.115f, a, cerca(pieLechuza - Offset(0f, w * 0.06f)), hacia(pieLechuza.x), dePie(pieLechuza, w * 0.011f), noche)
        utiles(apoyo, a, { p -> cerca(p) }, { p -> dePie(p, w * 0.008f) })

        // ---- al pie: el asiento plegable con el manto, las sandalias bajo el mueble y el cesto de los rollos
        val pieSandalias = Offset(w * 0.57f, suelo + w * 0.022f)
        sandalias(pieSandalias, w * 0.088f, a, maxOf(cerca(pieSandalias), 0.16f))
        val pieAsiento = Offset(w * 0.255f, suelo + w * 0.05f)
        asiento(pieAsiento, w * 0.2f, w * 0.118f, a, maxOf(cerca(pieAsiento), 0.2f), hacia(pieAsiento.x))
        val pieCesto = Offset(w * 0.925f, suelo + w * 0.06f)
        cestoRollos(pieCesto, w * 0.25f, w * 0.15f, a, maxOf(cerca(pieCesto), 0.2f), hacia(pieCesto.x))

        // ---- las llamas, encima de todo, con su propio color
        if (a.lumbre) lucernaLlamas(a)
        // El grano de papel, al final.
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    // ---------------------------------------------------------------- el muro

    /** El revoque pintado por zonas: el friso encalado con su meandro, los filetes, el paño azul, la franja roja y el zócalo. */
    private fun DrawScope.muro(d: DisposicionEstante, a: Aire, suelo: Float, ch: Float) {
        val u = density; val w = size.width
        val friso = w * 0.086f
        val dado = d.base.top - 7f * u
        val zoc = suelo - w * 0.034f
        val lejos = 0.09f
        drawRect(a.luz(HUESO, lejos), Offset.Zero, Size(w, friso))
        drawRect(a.luz(PANO, lejos), Offset(0f, friso), Size(w, dado - friso))
        drawRect(a.luz(ROJO, lejos), Offset(0f, dado), Size(w, zoc - dado))
        drawRect(a.luz(ZOCALO, lejos), Offset(0f, zoc), Size(w, suelo - zoc))
        val pano = Rect(0f, friso, w, dado)
        val veces = ((dado - friso) / (w * 0.5f)).coerceIn(1f, 14f)
        pinceladas(pano, a.luz(mezcla(PANO, SOMBRA_MUEBLE, 0.4f), lejos), 701, (46 * veces).toInt(), u, 0.1f)
        pinceladas(pano, a.luz(mezcla(PANO, LUZ_MUEBLE, 0.55f), lejos), 702, (34 * veces).toInt(), u, 0.11f)
        pinceladas(pano, a.luz(mezcla(PANO, EGEO, 0.45f), lejos), 703, (18 * veces).toInt(), u, 0.08f)
        pinceladas(Rect(0f, 0f, w, friso), a.luz(mezcla(HUESO, SOMBRA_MUEBLE, 0.3f), lejos), 704, 14, u, 0.08f)
        pinceladas(Rect(0f, dado, w, zoc), a.luz(mezcla(ROJO, LUZ_MUEBLE, 0.3f), lejos), 705, 20, u, 0.1f)
        pinceladas(Rect(0f, dado, w, zoc), a.luz(mezcla(ROJO, ZOCALO, 0.6f), lejos), 706, 14, u, 0.14f)
        pinceladas(Rect(0f, zoc, w, suelo), a.luz(mezcla(ZOCALO, LUZ_MUEBLE, 0.3f), lejos), 707, 14, u, 0.12f)
        // El meandro del friso, pintado a pincel en rojo oscuro entre dos líneas, y ya gastado: el encalado se lo comió a trechos.
        val tinta = a.luz(mezcla(ROJO, ZOCALO, 0.3f), lejos)
        val banda = Rect(-w * 0.012f, w * 0.022f, w * 1.02f, w * 0.056f)
        greca(banda, tinta)
        drawRect(tinta, Offset(0f, banda.top - w * 0.01f), Size(w, 1.1f * u))
        drawRect(tinta, Offset(0f, banda.bottom + w * 0.008f), Size(w, 1.1f * u))
        for (k in 0 until 11) {
            val x = w * azar(711, k); val an = w * (0.03f + 0.08f * azar(712, k))
            drawOval(a.luz(HUESO, lejos).copy(alpha = 0.5f + 0.45f * azar(713, k)), Offset(x - an / 2, banda.top - w * 0.014f + banda.height * azar(714, k)), Size(an, banda.height * (0.3f + 0.55f * azar(715, k))))
        }
        // Los filetes entre el friso y el paño, y las orillas pintadas a mano, que no son rectas del todo.
        drawRect(a.luz(ROJO, lejos), Offset(0f, friso - w * 0.013f), Size(w, w * 0.009f))
        drawRect(a.luz(EGEO, lejos), Offset(0f, friso - w * 0.003f), Size(w, w * 0.004f))
        for (k in 0 until 26) {
            val x = w * k / 25f
            drawOval(a.luz(PANO, lejos), Offset(x - w * 0.03f, friso - (0.2f + 1.3f * azar(716, k)) * u), Size(w * 0.06f, 3f * u))
            drawOval(a.luz(ROJO, lejos), Offset(x - w * 0.03f, dado - (0.6f + 1.4f * azar(717, k)) * u), Size(w * 0.06f, 3f * u))
            drawOval(a.luz(ZOCALO, lejos), Offset(x - w * 0.03f, zoc - (0.4f + 1.2f * azar(718, k)) * u), Size(w * 0.06f, 2.6f * u))
        }
        // La mancha de luz del día, arriba a la izquierda.
        if (!a.oscuro) mancha(Offset(w * 0.03f, w * 0.03f), w * 0.98f, luzMueble(if (a.noche) 0.14f else 0.5f))
        // Y hacia la derecha y abajo el muro se va quedando en penumbra.
        pinceladas(Rect(w * 0.55f, friso, w, ch), a.sombra(1f), 708, 16, u, 0.06f)
        // Los arreglos del revoque: parches un poco más claros, con el borde de la llana.
        listOf(Offset(w * 0.5f, w * 0.2f), Offset(w * 0.93f, w * 0.34f), Offset(w * 0.2f, ch + w * 0.6f)).forEachIndexed { i, o ->
            val pr = Path()
            for (k in 0 until 9) {
                val ang = 6.283f * k / 9
                val rr = w * (0.045f + 0.03f * azar(720 + i, k))
                val q = Offset(o.x + rr * cos(ang) * 1.4f, o.y + rr * sin(ang) * 0.8f)
                if (k == 0) pr.moveTo(q.x, q.y) else pr.lineTo(q.x, q.y)
            }
            pr.close()
            drawPath(pr, a.luz(mezcla(PANO, HUESO, 0.4f), lejos).copy(alpha = 0.3f))
        }
        // Grietas finas.
        fun grieta(desde: Offset, largo: Float, semilla: Int) {
            val g = Path().apply { moveTo(desde.x, desde.y) }
            var p = desde
            for (k in 0 until 7) {
                p += Offset((azar(semilla, k) - 0.45f) * largo * 0.35f, largo / 7f)
                g.lineTo(p.x, p.y)
            }
            drawPath(g, a.sombra(0.34f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
            drawPath(corrido(g, -0.8f * u, 0f), a.claro(0.28f), style = Stroke(0.5f * u))
        }
        grieta(Offset(w * 0.965f, 0f), w * 0.15f, 731)
        grieta(Offset(w * 0.27f, w * 0.09f), w * 0.07f, 732)
        // Un desconchón: el revoque saltó y se ve el adobe; el borde de arriba le da sombra y el de abajo toma la luz.
        val od = Offset(w * 0.6f, w * 0.11f)
        val s = w * 0.01f
        val des = poligono(
            od.x - 3.6f * s, od.y - 0.4f * s, od.x - 2.4f * s, od.y - 1.5f * s, od.x - 0.8f * s, od.y - 1.2f * s, od.x + 0.3f * s, od.y - 2f * s,
            od.x + 2f * s, od.y - 1.6f * s, od.x + 3.3f * s, od.y - 0.5f * s, od.x + 2.9f * s, od.y + 0.8f * s, od.x + 1.4f * s, od.y + 1.1f * s,
            od.x + 0.6f * s, od.y + 2f * s, od.x - 0.9f * s, od.y + 1.4f * s, od.x - 2.6f * s, od.y + 1.5f * s,
        )
        drawPath(des, a.luz(mezcla(TIERRA, CUERO, 0.5f), lejos + 0.1f))
        clipPath(des) {
            // La paja del adobe.
            for (k in 0 until 8) {
                val o = Offset(od.x + w * 0.05f * (azar(729, k) - 0.5f), od.y + w * 0.03f * (azar(730, k) - 0.5f))
                drawLine(a.luz(mezcla(TIERRA, HUESO, 0.5f), lejos + 0.1f), o, o + Offset(w * 0.008f, (azar(728, k) - 0.5f) * w * 0.006f), 0.7f * u)
            }
        }
        drawPath(borde(des, 1.6f * u, 2f * u), a.sombra(0.5f))
        drawPath(borde(des, -0.7f * u, -0.8f * u), a.luz(HUESO, lejos + 0.2f).copy(alpha = 0.7f))
        // De él baja una grieta.
        grieta(od + Offset(w * 0.006f, w * 0.02f), w * 0.05f, 727)
        // En el zócalo, donde roza el pie: desconchones chicos y la humedad que sube del suelo.
        for (k in 0 until 5) {
            val o = Offset(w * (0.04f + 0.92f * azar(735, k)), zoc + (suelo - zoc) * (0.25f + 0.6f * azar(736, k)))
            val rr = w * (0.004f + 0.006f * azar(737, k))
            val chip = poligono(o.x - rr * 1.8f, o.y, o.x - rr * 0.6f, o.y - rr * 0.9f, o.x + rr * 1.2f, o.y - rr * 0.6f, o.x + rr * 1.9f, o.y + rr * 0.3f, o.x + rr * 0.3f, o.y + rr * 0.9f)
            drawPath(chip, a.luz(mezcla(ZOCALO, HUESO, 0.3f), lejos))
            drawPath(borde(chip, 0.9f * u, 1.1f * u), a.sombra(0.45f))
        }
        pinceladas(Rect(0f, suelo - w * 0.05f, w, suelo), a.sombra(1f), 738, 18, u, 0.12f)
        // El ejercicio de un niño, rayado con un clavo en el paño: alfa, beta, gamma, delta.
        grafito(Offset(w * 0.07f, w * 0.408f), w * 0.03f, a.luz(HUESO, 0.5f).copy(alpha = if (a.oscuro) 0.5f else 0.62f), a.sombra(0.3f))
    }

    /** Cuatro letras rayadas en el revoque con mano torpe: se leen como trazos. */
    private fun DrawScope.grafito(o: Offset, alto: Float, color: Color, sombra: Color) {
        val u = density
        val p = Path()
        var x = o.x
        p.moveTo(x, o.y); p.lineTo(x + alto * 0.36f, o.y - alto); p.lineTo(x + alto * 0.74f, o.y + alto * 0.05f)
        p.moveTo(x + alto * 0.16f, o.y - alto * 0.4f); p.lineTo(x + alto * 0.6f, o.y - alto * 0.34f)
        x += alto * 1.15f
        p.moveTo(x, o.y + alto * 0.06f); p.lineTo(x + alto * 0.05f, o.y - alto * 0.92f)
        p.cubicTo(x + alto * 0.62f, o.y - alto * 1.0f, x + alto * 0.55f, o.y - alto * 0.5f, x + alto * 0.05f, o.y - alto * 0.48f)
        p.cubicTo(x + alto * 0.74f, o.y - alto * 0.5f, x + alto * 0.66f, o.y + alto * 0.08f, x, o.y + alto * 0.02f)
        x += alto * 1.1f
        p.moveTo(x + alto * 0.06f, o.y - alto * 0.04f); p.lineTo(x, o.y - alto * 0.88f); p.lineTo(x + alto * 0.56f, o.y - alto * 0.96f)
        x += alto * 0.95f
        p.moveTo(x, o.y - alto * 0.1f); p.lineTo(x + alto * 0.4f, o.y - alto * 0.94f); p.lineTo(x + alto * 0.8f, o.y - alto * 0.04f); p.lineTo(x - alto * 0.04f, o.y - alto * 0.12f)
        drawPath(corrido(p, 0.7f * u, 0.8f * u), sombra, style = Stroke(0.9f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(p, color, style = Stroke(1.2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    /** El nicho de la lucerna, abierto en el adobe y encalado por dentro: el dintel en sombra, la jamba que mira a la luz, el alféizar y el hollín. */
    private fun DrawScope.nicho(a: Aire, llama: Offset) {
        val u = density; val w = size.width
        val r = Rect(w * 0.062f, w * 0.172f, w * 0.236f, w * 0.318f)
        val forma = Path().apply { addRoundRect(RoundRect(r.left, r.top, r.right, r.bottom, CornerRadius(w * 0.012f), CornerRadius(w * 0.009f), CornerRadius(w * 0.004f), CornerRadius(w * 0.005f))) }
        // El hollín que sube por el muro, encima del nicho: un tizne tenue, no una nube.
        mancha(Offset(r.center.x + w * 0.006f, r.top - w * 0.012f), w * 0.075f, a.sombra(0.3f))
        // El canto del revoque alrededor del hueco: abajo y a la derecha toma la luz.
        drawPath(corrido(forma, 1.3f * u, 1.5f * u), a.luz(mezcla(HUESO, PANO, 0.2f), 0.7f))
        drawPath(forma, a.luz(mezcla(HUESO, SOMBRA_MUEBLE, if (a.lumbre) 0.06f else 0.1f), 0.92f))
        val alf = r.bottom - w * 0.02f
        val fondoN = w * 0.016f
        clipPath(forma) {
            pinceladas(r, a.sombra(1f), 739, 8, u, 0.05f)
            if (a.lumbre) mancha(Offset(llama.x, llama.y + w * 0.01f), r.width * 0.8f, LLAMA.copy(alpha = if (a.oscuro) 0.55f else 0.38f))
            // El hollín de tantas noches en lo alto del nicho.
            drawOval(a.sombra(0.22f), Offset(r.left + r.width * 0.16f, r.top - r.height * 0.2f), Size(r.width * 0.68f, r.height * 0.42f))
            // La sombra que el dintel y la jamba izquierda echan sobre el fondo: una escuadra, con el borde sesgado.
            if (!a.lumbre) {
                drawPath(poligono(r.left, r.top, r.right, r.top, r.right, r.top + r.height * 0.2f, r.left + r.width * 0.24f, r.top + r.height * 0.3f, r.left + r.width * 0.2f, alf, r.left, alf), a.sombra(0.3f))
                drawPath(poligono(r.left, r.top, r.right, r.top, r.right, r.top + r.height * 0.1f, r.left + r.width * 0.12f, r.top + r.height * 0.16f, r.left + r.width * 0.1f, alf, r.left, alf), a.sombra(0.2f))
            } else {
                drawRect(a.sombra(0.2f), r.topLeft, Size(r.width, r.height * 0.14f))
            }
            // El grueso del muro: la jamba derecha, que mira a la luz, y el alféizar, visto apenas desde arriba.
            drawPath(poligono(r.right, r.top, r.right, r.bottom, r.right - fondoN * 0.5f, alf, r.right - fondoN * 0.5f, r.top + fondoN * 0.4f), a.luz(mezcla(HUESO, LUZ_MUEBLE, 0.5f), 1f))
            drawPath(poligono(r.left, r.bottom, r.right, r.bottom, r.right - fondoN * 0.5f, alf, r.left + fondoN * 0.3f, alf), a.luz(mezcla(HUESO, LUZ_MUEBLE, 0.4f), 1f))
            drawLine(a.sombra(0.3f), Offset(r.left, alf), Offset(r.right - fondoN * 0.5f, alf), 0.9f * u)
            drawLine(a.sombra(0.2f), Offset(r.right - fondoN * 0.5f, alf), Offset(r.right - fondoN * 0.5f, r.top), 0.7f * u)
        }
        drawPath(borde(forma, -1.5f * u, -1.9f * u), a.sombra(if (a.lumbre) 0.22f else 0.5f))
        lucerna(Offset(llama.x, w * LUCERNA_Y), w * 0.098f, a)
    }

    /** La lucerna de barro de dos picos, de perfil: el cuerpo bajo con su collarín, un pico a cada lado y la mecha en cada uno. */
    private fun DrawScope.lucerna(pie: Offset, an: Float, a: Aire) {
        val u = density
        val k = 0.95f
        val alto = an * 0.27f
        val barro = a.luz(TERRACOTA, k)
        contacto(Offset(pie.x + 2f * u, pie.y), an * 0.9f, alto * 0.3f, a, 0.3f)
        for (l in listOf(-1f, 1f)) {
            val pico = poligono(pie.x + l * an * 0.22f, pie.y - alto * 0.86f, pie.x + l * an * 0.5f, pie.y - alto * 0.98f, pie.x + l * an * 0.52f, pie.y - alto * 0.66f, pie.x + l * an * 0.24f, pie.y - alto * 0.12f)
            drawPath(pico, barro)
            drawPath(pico, a.sombra(if (l > 0) 0.3f else 0.12f))
            // La boca de la mecha, tiznada.
            drawOval(a.luz(BARNIZ, k), Offset(pie.x + l * an * 0.5f - an * 0.035f, pie.y - alto * 1.02f), Size(an * 0.07f, alto * 0.2f))
        }
        val cuerpo = perfil(pie.x, pie.y, an, floatArrayOf(0.1f, -0.29f, 0.24f, -0.26f, 0.31f, -0.16f, 0.27f, -0.05f, 0.17f, 0f))
        drawPath(cuerpo, barro)
        clipPath(cuerpo) {
            drawRect(a.luz(mezcla(TERRACOTA, BARNIZ, 0.55f), k), Offset(pie.x - an, pie.y - alto * 0.62f), Size(an * 2, alto * 0.14f))
        }
        redondo(cuerpo, a, 0.5f, 1f, brillo = 0.2f)
        // El collarín del agujero por donde se llena.
        drawRect(barro, Offset(pie.x - an * 0.085f, pie.y - alto * 1.24f), Size(an * 0.17f, alto * 0.2f))
        drawOval(a.luz(mezcla(TERRACOTA, HUESO, 0.3f), k), Offset(pie.x - an * 0.1f, pie.y - alto * 1.32f), Size(an * 0.2f, alto * 0.16f))
        drawOval(a.luz(BARNIZ, k), Offset(pie.x - an * 0.06f, pie.y - alto * 1.3f), Size(an * 0.12f, alto * 0.1f))
        if (!a.lumbre) for (l in listOf(-1f, 1f)) drawLine(a.luz(BARNIZ, k), Offset(pie.x + l * an * 0.5f, pie.y - alto * 0.94f), Offset(pie.x + l * an * 0.51f, pie.y - alto * 1.2f), 1f * u)
    }

    /** Las dos llamas de la lucerna, con su color propio. */
    private fun DrawScope.lucernaLlamas(a: Aire) {
        val w = size.width
        val alf = w * LUCERNA_Y
        val an = w * 0.098f
        for (l in listOf(-1f, 1f)) {
            val p = Offset(w * LAMP_X + l * an * 0.5f, alf - an * 0.27f)
            val s = w * 0.03f * (if (l < 0) 1f else 0.9f)
            drawCircle(LLAMA.copy(alpha = 0.16f), s * 1.05f, p - Offset(0f, s * 0.5f))
            drawCircle(LLAMA.copy(alpha = 0.2f), s * 0.66f, p - Offset(0f, s * 0.5f))
            fun lengua(k: Float, ladeo: Float) = Path().apply {
                moveTo(p.x - s * 0.17f * k, p.y)
                cubicTo(p.x - s * 0.3f * k, p.y - s * 0.4f * k, p.x + ladeo * s - s * 0.05f * k, p.y - s * 0.7f * k, p.x + ladeo * s, p.y - s * 1.1f * k)
                cubicTo(p.x + ladeo * s + s * 0.12f * k, p.y - s * 0.6f * k, p.x + s * 0.3f * k, p.y - s * 0.35f * k, p.x + s * 0.17f * k, p.y)
                close()
            }
            drawPath(lengua(1f, 0.05f * l), Color(0xFFFF9A2E))
            drawPath(lengua(0.72f, 0.03f * l), LLAMA)
            drawPath(lengua(0.4f, 0f), Color(0xFFFFF6DC))
        }
    }

    // ---------------------------------------------------------------- el armazón

    /**
     * Una tabla gruesa: su cara de arriba, vista apenas ([fondo]), y el canto de delante con su veta, sus muescas y su luz. De
     * noche se pinta por tramos, cada uno con la luz que le llega ([cerca], por la x de su centro).
     */
    private fun DrawScope.tablon(cara: Rect, fondo: Float, semilla: Int, a: Aire, cerca: (Float) -> Float) {
        val u = density
        val tramos = if (a.lumbre) 28 else 1
        for (t in 0 until tramos) {
            val xa = cara.left + cara.width * t / tramos; val xb = cara.left + cara.width * (t + 1) / tramos + 0.6f
            val k = cerca((xa + xb) / 2)
            drawRect(a.luz(mezcla(MADERA, LUZ_MUEBLE, 0.3f), k), Offset(xa, cara.top - fondo), Size(xb - xa, fondo))
            drawRect(a.luz(MADERA, k), Offset(xa, cara.top), Size(xb - xa, cara.height))
            if (a.oscuro) drawRect(FILO_MUEBLE.copy(alpha = 0.08f + 0.55f * k), Offset(xa, cara.top), Size(xb - xa, 1.1f * u))
        }
        clipRect(cara.left, cara.top - fondo, cara.right, cara.bottom) {
            vetas(cara, MADERA_OSC.copy(alpha = if (a.oscuro) 0.5f else 0.42f), semilla, densidad = 0.6f, grosor = 0.8f, ondulacion = 0.7f)
            vetas(Rect(cara.left, cara.top - fondo, cara.right, cara.top), MADERA_OSC.copy(alpha = 0.2f), semilla + 3, densidad = 0.9f, grosor = 0.5f, ondulacion = 0.3f)
            // Un nudo, una mancha de tinta vieja y las muescas del canto.
            val nx = cara.left + cara.width * (0.12f + 0.76f * azar(semilla, 7))
            val nr = cara.height * 0.2f
            drawOval(MADERA_OSC.copy(alpha = 0.55f), Offset(nx - nr * 1.2f, cara.center.y - nr * 0.6f), Size(2.4f * nr, 1.2f * nr))
            drawOval(MADERA_OSC.copy(alpha = 0.3f), Offset(nx - nr * 2f, cara.center.y - nr), Size(4f * nr, 2f * nr), style = Stroke(0.6f * u))
            val mx = cara.left + cara.width * (0.1f + 0.8f * azar(semilla, 9))
            drawOval(a.sombra(0.2f), Offset(mx, cara.top + cara.height * 0.2f), Size(cara.height * 0.9f, cara.height * 0.5f))
            for (j in 0 until 5) {
                val x = cara.left + cara.width * azar(semilla, 11 + j)
                drawPath(poligono(x, cara.top, x + 2.6f * u, cara.top, x + 1.2f * u, cara.top + 2.2f * u), a.sombra(0.4f))
            }
        }
        if (!a.oscuro) drawRect(a.claro(0.5f), cara.topLeft, Size(cara.width, 1.1f * u))
        drawLine(a.sombra(0.4f), Offset(cara.left, cara.top - fondo), Offset(cara.right, cara.top - fondo), 0.7f * u)
        drawRect(a.sombra(0.38f), Offset(cara.left, cara.bottom - 1.6f * u), Size(cara.width, 1.6f * u))
    }

    /** Una riostra en el rincón [esquina] (hacia [lado]: 1 a la derecha, -1 a la izquierda): el taco en escuadra con su clavija y su sombra. */
    private fun DrawScope.riostra(esquina: Offset, lado: Float, s: Float, a: Aire, cerca: Float) {
        val u = density
        val forma = poligono(esquina.x, esquina.y, esquina.x + lado * s, esquina.y, esquina.x + lado * s, esquina.y + s * 0.3f, esquina.x + lado * s * 0.3f, esquina.y + s, esquina.x, esquina.y + s)
        drawPath(corrido(forma, 2.4f * u, 2.6f * u), a.sombra(if (a.oscuro) 0.4f else 0.3f))
        drawPath(forma, a.luz(mezcla(MADERA, MADERA_OSC, 0.22f), cerca))
        drawPath(borde(forma, -lado * 1.2f * u, -1.3f * u), a.sombra(0.34f))
        drawLine(a.claro(0.3f), Offset(esquina.x + lado * s * 0.3f, esquina.y + s), Offset(esquina.x + lado * s, esquina.y + s * 0.3f), 0.7f * u)
        clavija(esquina + Offset(lado * s * 0.36f, s * 0.38f), 1.5f * u, a.luz(mezcla(MADERA, LUZ_MUEBLE, 0.22f), cerca), a, cerca)
    }

    /** Un poste cuadrado: la cara con su veta, la arista de la luz y el lado de la sombra; de noche se apaga hacia abajo, repisa a repisa. */
    private fun DrawScope.poste(r: Rect, semilla: Int, a: Aire, repisas: Int, ch: Float, fila: Float, haciaX: Float) {
        val u = density
        val fx = r.center.x / size.width
        val tramos = if (a.lumbre) (r.height / (fila * 0.25f)).toInt().coerceIn(4, 80) else 1
        for (t in 0 until tramos) {
            val ya = r.top + r.height * t / tramos; val yb = r.top + r.height * (t + 1) / tramos + 0.6f
            drawRect(a.luz(MADERA, cercaCuerpo(((ya + yb) / 2 - ch) / fila, repisas, fx)), Offset(r.left, ya), Size(r.width, yb - ya))
        }
        clipRect(r.left, r.top, r.right, r.bottom) {
            vetas(r, MADERA_OSC.copy(alpha = if (a.oscuro) 0.5f else 0.42f), semilla, densidad = 0.55f, vertical = true, grosor = 0.8f, ondulacion = 0.8f)
            for (j in 0 until 3) {
                val ny = r.top + r.height * (0.12f + 0.3f * j + 0.15f * azar(semilla, 20 + j))
                val nr = r.width * 0.16f
                drawOval(MADERA_OSC.copy(alpha = 0.5f), Offset(r.left + r.width * (0.25f + 0.4f * azar(semilla, 30 + j)) - nr * 0.6f, ny - nr), Size(1.2f * nr, 2f * nr))
            }
        }
        drawRect(a.claro(0.28f), r.topLeft, Size(r.width * 0.2f, r.height))
        drawRect(a.sombra(0.3f), Offset(r.right - r.width * 0.28f, r.top), Size(r.width * 0.28f, r.height))
        drawRect(a.sombra(0.3f), Offset(r.right - 0.8f * u, r.top), Size(0.8f * u, r.height))
        if (a.oscuro) {
            val x = if (haciaX > 0) r.right - 1.2f * u else r.left
            val k = cercaCuerpo(0.2f, repisas, fx)
            drawRect(FILO_MUEBLE.copy(alpha = 0.5f * k), Offset(x, r.top), Size(1.2f * u, fila * 0.9f))
        }
    }

    // ---------------------------------------------------------------- lo de escribir y lo de aprender, sobre el tablero

    /**
     * El escudo de entrenamiento (un aspis chico), apoyado en el muro: el canto de bronce, el campo pintado de rojo con un
     * delfín sobre una ola, las rozaduras de los golpes y la panza que toma la luz arriba a la izquierda.
     */
    private fun DrawScope.aspis(c: Offset, r: Float, apoyo: Float, a: Aire, cerca: Float, haciaX: Float, caida: Offset) {
        val u = density
        val ry = r * 0.97f
        val disco = ovalo(c, r, ry)
        translate(caida.x, caida.y) { drawPath(disco, a.sombra(if (a.oscuro) 0.42f else 0.3f)) }
        contacto(Offset(c.x + r * 0.1f, apoyo), r * 0.9f, r * 0.07f, a, 0.38f)
        val bronce = a.luz(BRONCE, cerca)
        // El grueso del canto, que se ve por abajo porque el escudo está echado hacia atrás.
        drawPath(corrido(disco, 0f, r * 0.03f), a.luz(mezcla(BRONCE, BARNIZ, 0.5f), cerca))
        drawPath(disco, bronce)
        drawPath(borde(disco, r * 0.05f, r * 0.05f), a.claro(0.45f))
        drawPath(borde(disco, -r * 0.05f, -r * 0.05f), a.sombra(0.3f))
        // Los clavos del canto.
        for (k in 0 until 20) {
            val ang = 6.283f * k / 20 + 0.1f
            drawCircle(a.luz(mezcla(BRONCE, BARNIZ, 0.45f), cerca), r * 0.012f, Offset(c.x + r * 0.925f * cos(ang), c.y + ry * 0.925f * sin(ang)))
        }
        val campo = ovalo(c, r * 0.85f, ry * 0.85f)
        drawPath(campo, a.luz(ROJO, cerca))
        drawPath(campo, a.sombra(0.4f), style = Stroke(0.8f * u))
        clipPath(campo) {
            pinceladas(Rect(c.x - r, c.y - r, c.x + r, c.y + r), a.luz(mezcla(ROJO, ZOCALO, 0.5f), cerca), 801, 16, u, 0.16f)
            pinceladas(Rect(c.x - r, c.y - r, c.x + r, c.y + r), a.luz(mezcla(ROJO, TERRACOTA, 0.6f), cerca), 802, 12, u, 0.14f)
        }
        // El delfín, en crema, que salta a la izquierda, y la ola de abajo.
        val crema = a.luz(mezcla(HUESO, PAPIRO, 0.25f), cerca + 0.08f)
        val g = r * 0.56f
        val t = Offset(c.x - r * 0.02f, c.y - r * 0.04f)
        val delfin = Path().apply {
            moveTo(t.x - 1.04f * g, t.y + 0.42f * g)
            lineTo(t.x - 0.78f * g, t.y + 0.2f * g)
            cubicTo(t.x - 0.66f * g, t.y - 0.5f * g, t.x + 0.3f * g, t.y - 0.72f * g, t.x + 0.74f * g, t.y - 0.04f * g)
            lineTo(t.x + 1.04f * g, t.y - 0.3f * g); lineTo(t.x + 0.9f * g, t.y + 0.1f * g); lineTo(t.x + 1.08f * g, t.y + 0.4f * g); lineTo(t.x + 0.66f * g, t.y + 0.2f * g)
            cubicTo(t.x + 0.34f * g, t.y - 0.16f * g, t.x - 0.3f * g, t.y - 0.06f * g, t.x - 0.7f * g, t.y + 0.38f * g)
            close()
            // La aleta del lomo y la del pecho.
            moveTo(t.x - 0.1f * g, t.y - 0.5f * g); lineTo(t.x + 0.18f * g, t.y - 0.86f * g); lineTo(t.x + 0.26f * g, t.y - 0.42f * g); close()
            moveTo(t.x - 0.4f * g, t.y + 0.04f * g); cubicTo(t.x - 0.36f * g, t.y + 0.2f * g, t.x - 0.26f * g, t.y + 0.3f * g, t.x - 0.1f * g, t.y + 0.34f * g); lineTo(t.x - 0.16f * g, t.y - 0.02f * g); close()
        }
        drawPath(delfin, crema)
        drawCircle(a.luz(ROJO, cerca), g * 0.045f, Offset(t.x - 0.64f * g, t.y + 0.08f * g))
        val ola = Path().apply {
            moveTo(t.x - 1.05f * g, t.y + 0.86f * g)
            for (k in 0 until 4) {
                val xk = t.x - 1.05f * g + k * 0.52f * g
                cubicTo(xk + 0.14f * g, t.y + 0.66f * g, xk + 0.38f * g, t.y + 0.66f * g, xk + 0.52f * g, t.y + 0.86f * g)
            }
        }
        drawPath(ola, crema, style = Stroke(r * 0.03f, cap = StrokeCap.Round))
        // Las rozaduras: la pintura saltada deja ver la madera, y un tajo largo.
        clipPath(campo) {
            for (k in 0 until 7) {
                val ang = 6.283f * azar(803, k)
                val rr = r * (0.3f + 0.5f * azar(804, k))
                val o = Offset(c.x + rr * cos(ang), c.y + rr * sin(ang))
                rotate(40f * azar(805, k) - 20f, o) { drawOval(a.luz(mezcla(MADERA, ROJO, 0.3f), cerca), Offset(o.x - r * 0.03f, o.y - r * 0.008f), Size(r * (0.03f + 0.05f * azar(806, k)), r * 0.016f)) }
            }
            drawLine(a.luz(mezcla(MADERA, ROJO, 0.35f), cerca), Offset(c.x + r * 0.2f, c.y - r * 0.62f), Offset(c.x + r * 0.5f, c.y - r * 0.36f), 0.9f * u, StrokeCap.Round)
            drawLine(a.sombra(0.3f), Offset(c.x + r * 0.2f, c.y - r * 0.6f), Offset(c.x + r * 0.5f, c.y - r * 0.34f), 0.6f * u, StrokeCap.Round)
        }
        // La panza: el claro arriba a la izquierda y la sombra abajo a la derecha (de noche, del lado contrario a la lucerna).
        val sl = if (a.oscuro) -haciaX else 1f
        drawPath(borde(campo, -sl * r * 0.3f, -r * 0.24f), a.sombra(0.2f))
        drawPath(borde(campo, -sl * r * 0.13f, -r * 0.1f), a.sombra(0.2f))
        clipPath(campo) {
            mancha(Offset(c.x - sl * r * 0.38f, c.y - r * 0.42f), r * 0.6f, if (a.oscuro) FILO_MUEBLE.copy(alpha = 0.3f * cerca) else a.claro(0.26f))
        }
        if (a.oscuro) drawPath(borde(disco, sl * r * 0.03f, r * 0.02f), FILO_MUEBLE.copy(alpha = (0.1f + 0.6f * cerca).coerceAtMost(0.7f)))
    }

    /**
     * La funda del aulós doble (sybene), de piel moteada, colgada de su clavija: asoman las dos cañas con su boquilla, a un
     * lado lleva la cajita de las lengüetas y abajo, un fleco.
     */
    private fun DrawScope.fundaAulos(clavo: Offset, largo: Float, a: Aire, cerca: Float, haciaX: Float, caida: Offset) {
        val u = density
        val x = clavo.x; val an = largo * 0.25f
        val y0 = clavo.y + largo * 0.3f; val y1 = clavo.y + largo
        val alto = y1 - y0
        // La bolsa: ancha en la boca y más estrecha abajo, donde acaba en una punta roma.
        val bolsa = Path().apply {
            moveTo(x - an * 0.5f, y0)
            cubicTo(x - an * 0.52f, y0 + alto * 0.4f, x - an * 0.44f, y0 + alto * 0.8f, x - an * 0.2f, y1 - an * 0.06f)
            cubicTo(x - an * 0.08f, y1 + an * 0.08f, x + an * 0.08f, y1 + an * 0.08f, x + an * 0.2f, y1 - an * 0.06f)
            cubicTo(x + an * 0.44f, y0 + alto * 0.8f, x + an * 0.52f, y0 + alto * 0.4f, x + an * 0.5f, y0)
            close()
        }
        val caja = Rect(x + an * 0.44f, y0 + alto * 0.1f, x + an * 0.9f, y0 + alto * 0.36f)
        // Las dos cañas que asoman por la boca, ladeadas: por dónde va cada una.
        val canas = listOf(-1f, 1f).map { l -> Offset(x + l * an * 0.17f - an * 0.04f, y0 + alto * 0.06f) to Offset(x + l * an * 0.2f - an * 0.34f, y0 - largo * 0.25f) }
        rotate(9f, clavo) {
            translate(caida.x, caida.y) {
                val sc = a.sombra(if (a.oscuro) 0.42f else 0.22f)
                drawPath(bolsa, sc)
                drawRect(sc, caja.topLeft, caja.size)
                canas.forEach { (p0, p1) -> drawLine(sc, p0, p1, an * 0.17f, StrokeCap.Round) }
            }
            // Las cañas, de boj, con sus agujeros, el bulbo (el holmos) y la lengüeta.
            canas.forEach { (p0, p1) ->
                drawLine(a.luz(mezcla(BOJ, MADERA, 0.25f), cerca), p0, p1, an * 0.17f, StrokeCap.Butt)
                drawLine(a.claro(0.4f), p0 - Offset(an * 0.05f, 0f), p1 - Offset(an * 0.05f, 0f), an * 0.035f)
                drawLine(a.sombra(0.25f), p0 + Offset(an * 0.055f, 0f), p1 + Offset(an * 0.055f, 0f), an * 0.04f)
                for (j in 1..2) drawCircle(a.luz(BARNIZ, cerca), an * 0.03f, p0 + (p1 - p0) * (0.3f + 0.22f * j))
                val bulbo = p1 + (p1 - p0) * 0.08f
                drawOval(a.luz(mezcla(BOJ, MADERA_OSC, 0.45f), cerca), Offset(bulbo.x - an * 0.13f, bulbo.y - an * 0.15f), Size(an * 0.26f, an * 0.3f))
                drawLine(a.luz(PAPIRO, cerca), bulbo, bulbo + (p1 - p0) * 0.22f, an * 0.07f, StrokeCap.Round)
            }
            // El cordel de colgar, a los dos lados, y la clavija.
            val cuerda = a.luz(mezcla(CUERO, HUESO, 0.4f), cerca)
            drawLine(cuerda, clavo, Offset(x - an * 0.46f, y0 + alto * 0.03f), 1f * u)
            drawLine(cuerda, clavo, Offset(x + an * 0.46f, y0 + alto * 0.03f), 1f * u)
            // La cajita de las lengüetas, de madera, con su tapa.
            drawRect(a.luz(MADERA, cerca), caja.topLeft, caja.size)
            drawRect(a.claro(0.3f), caja.topLeft, Size(caja.width, 1f * u))
            drawRect(a.sombra(0.3f), Offset(caja.right - caja.width * 0.3f, caja.top), Size(caja.width * 0.3f, caja.height))
            drawLine(a.sombra(0.45f), Offset(caja.left, caja.top + caja.height * 0.22f), Offset(caja.right, caja.top + caja.height * 0.22f), 0.7f * u)
            // La piel: clara, con sus motas y la boca vuelta de cuero más oscuro.
            val piel = a.luz(mezcla(PAPIRO, CUERO, 0.3f), cerca)
            drawPath(bolsa, piel)
            clipPath(bolsa) {
                // Las motas de la piel, en corros de tres.
                for (k in 0 until 9) {
                    val o = Offset(x + an * (azar(811, k) - 0.5f) * 0.8f, y0 + alto * (0.22f + 0.72f * k / 8f))
                    for (j in 0 until 3) {
                        val ang = 2.1f * j + azar(812, k) * 2f
                        val rr = an * (0.035f + 0.025f * azar(813, k + j))
                        drawOval(a.luz(mezcla(CUERO, BARNIZ, 0.3f), cerca), Offset(o.x + an * 0.075f * cos(ang) - rr, o.y + an * 0.075f * sin(ang) - rr * 0.8f), Size(rr * 2f, rr * 1.6f))
                    }
                }
                // La boca vuelta, de cuero más oscuro, con su pespunte y el borde en picos.
                drawRect(a.luz(CUERO, cerca), Offset(x - an, y0 - 1f), Size(an * 2, alto * 0.13f))
                for (k in 0 until 7) drawPath(poligono(x - an * 0.56f + k * an * 0.16f, y0 + alto * 0.13f, x - an * 0.4f + k * an * 0.16f, y0 + alto * 0.13f, x - an * 0.48f + k * an * 0.16f, y0 + alto * 0.17f), a.luz(CUERO, cerca))
                drawRect(a.sombra(0.26f), Offset(x - an, y0 + alto * 0.13f), Size(an * 2, 1f * u))
                for (k in 0 until 7) drawLine(a.luz(HUESO, cerca), Offset(x - an * 0.46f + k * an * 0.14f, y0 + alto * 0.06f), Offset(x - an * 0.4f + k * an * 0.14f, y0 + alto * 0.06f), 0.7f * u)
                // Un pliegue a lo largo.
                drawLine(a.sombra(0.16f), Offset(x + an * 0.12f, y0 + alto * 0.2f), Offset(x + an * 0.04f, y1), 1f * u)
            }
            redondo(bolsa, a, cerca, haciaX, fuerza = 1.1f)
            clavija(clavo, largo * 0.022f, a.luz(MADERA_OSC, cerca), a, cerca)
        }
    }

    /** El saquito de las tabas (astrágalos), de cuero, fruncido con su cordón rojo y colgado de un clavo. */
    private fun DrawScope.saquito(clavo: Offset, s: Float, a: Aire, cerca: Float, haciaX: Float, caida: Offset) {
        val u = density
        val c = clavo + Offset(0f, s * 1.5f)
        val forma = Path().apply {
            moveTo(c.x - s * 0.16f, c.y - s * 0.62f)
            cubicTo(c.x - s * 0.7f, c.y - s * 0.3f, c.x - s * 0.66f, c.y + s * 0.56f, c.x, c.y + s * 0.6f)
            cubicTo(c.x + s * 0.66f, c.y + s * 0.56f, c.x + s * 0.7f, c.y - s * 0.3f, c.x + s * 0.16f, c.y - s * 0.62f)
            close()
        }
        val boca = poligono(c.x - s * 0.3f, c.y - s * 0.9f, c.x + s * 0.3f, c.y - s * 0.92f, c.x + s * 0.15f, c.y - s * 0.6f, c.x - s * 0.15f, c.y - s * 0.6f)
        translate(caida.x, caida.y) { drawPath(forma, a.sombra(if (a.oscuro) 0.4f else 0.2f)); drawPath(boca, a.sombra(if (a.oscuro) 0.4f else 0.2f)) }
        drawLine(a.luz(ROJO, cerca), clavo, Offset(c.x, c.y - s * 0.64f), 1f * u)
        val cuero = a.luz(mezcla(CUERO, TERRACOTA, 0.35f), cerca)
        drawPath(boca, cuero)
        drawPath(forma, cuero)
        // Los bultos de las tabas, que se marcan en el cuero, y los pliegues del fruncido.
        clipPath(forma) {
            for (k in 0 until 4) {
                val o = Offset(c.x + s * (-0.3f + 0.22f * k), c.y + s * (0.12f + 0.16f * (k % 2)))
                drawArc(a.claro(0.3f), 180f, 150f, false, Offset(o.x - s * 0.14f, o.y - s * 0.12f), Size(s * 0.28f, s * 0.24f), style = Stroke(0.8f * u))
                drawArc(a.sombra(0.3f), 0f, 150f, false, Offset(o.x - s * 0.14f, o.y - s * 0.12f), Size(s * 0.28f, s * 0.24f), style = Stroke(0.8f * u))
            }
            for (k in -1..1) drawLine(a.sombra(0.25f), Offset(c.x + k * s * 0.07f, c.y - s * 0.6f), Offset(c.x + k * s * 0.22f, c.y - s * 0.2f), 0.6f * u)
        }
        redondo(forma, a, cerca, haciaX)
        drawLine(a.luz(ROJO, cerca), Offset(c.x - s * 0.18f, c.y - s * 0.62f), Offset(c.x + s * 0.18f, c.y - s * 0.62f), s * 0.09f, StrokeCap.Round)
        drawLine(a.luz(ROJO, cerca), Offset(c.x + s * 0.16f, c.y - s * 0.62f), Offset(c.x + s * 0.34f, c.y - s * 0.3f), s * 0.05f, StrokeCap.Round)
        drawCircle(a.sombra(0.4f), 1.6f * u, clavo + Offset(0.8f * u, 0.8f * u))
        drawCircle(a.luz(BRONCE, cerca), 1.4f * u, clavo)
    }

    /**
     * El haz de rollos de papiro, atado y tumbado sobre el tablero con las bocas hacia fuera: tres abajo y dos encima, cada
     * uno con las vueltas de su hoja y su umbilicus, y de dos cuelga la etiqueta (el sillybos).
     */
    private fun DrawScope.hazRollos(pie: Offset, d: Float, a: Aire, cerca: Float, haciaX: Float, caida: Offset) {
        val u = density
        val r = d / 2
        val fondo = Offset(d * 0.62f, -d * 0.2f)
        val nrm = Offset(-fondo.y, fondo.x) / fondo.getDistance() * r
        val bocas = listOf(Offset(-d * 1.02f, -r), Offset(0f, -r), Offset(d * 1.0f, -r), Offset(-d * 0.52f, -r - d * 0.85f), Offset(d * 0.5f, -r - d * 0.85f)).map { pie + it }
        val sc = a.sombra(if (a.oscuro) 0.4f else 0.2f)
        bocas.forEach { c -> drawCircle(sc, r, c + fondo + caida) }
        contacto(Offset(pie.x + d * 0.4f, pie.y), d * 3.6f, d * 0.2f, a)
        val papiro = a.luz(PAPIRO, cerca)
        val atadura = a.luz(ROJO, cerca)
        bocas.forEachIndexed { i, c ->
            val tono = mezcla(papiro, a.luz(if (i % 2 == 0) MIMBRE else HUESO, cerca), 0.22f)
            // El cuerpo, que se va hacia el fondo, con su lado de arriba a la luz y la atadura a media caña.
            drawCircle(mezcla(tono, a.hondo, 0.3f), r, c + fondo)
            val cuerpo = poligono((c + nrm).x, (c + nrm).y, (c + nrm + fondo).x, (c + nrm + fondo).y, (c - nrm + fondo).x, (c - nrm + fondo).y, (c - nrm).x, (c - nrm).y)
            drawPath(cuerpo, mezcla(tono, a.hondo, 0.18f))
            drawLine(a.claro(0.4f), c - nrm * 0.8f + fondo * 0.1f, c - nrm * 0.8f + fondo, r * 0.22f)
            val m = c + fondo * 0.55f
            drawArc(atadura, 205f, 190f, false, Offset(m.x - r, m.y - r), Size(d, d), style = Stroke(1.5f * u))
            // La boca: las vueltas de la hoja, cada vez más prietas, y el umbilicus de madera en el medio.
            drawCircle(mezcla(tono, a.luz(HUESO, cerca), 0.3f), r, c)
            for (j in 1..3) drawCircle(a.luz(mezcla(PAPIRO, CUERO, 0.55f), cerca), r * (1f - 0.24f * j), c + Offset(r * 0.03f * j, r * 0.02f * j), style = Stroke(0.6f * u))
            val boca = ovalo(c, r, r)
            drawPath(borde(boca, -r * 0.3f, -r * 0.24f), a.sombra(0.22f))
            drawPath(borde(boca, r * 0.14f, r * 0.14f), a.claro(0.4f))
            drawCircle(a.luz(MADERA_OSC, cerca), r * 0.2f, c)
            drawCircle(a.claro(0.4f), r * 0.07f, c - Offset(r * 0.06f, r * 0.06f))
            drawPath(boca, a.sombra(0.3f), style = Stroke(0.5f * u))
            if (a.oscuro) drawArc(FILO_MUEBLE.copy(alpha = 0.6f * cerca), if (haciaX > 0) -70f else 150f, 100f, false, Offset(c.x - r, c.y - r), Size(d, d), style = Stroke(1f * u))
        }
        // Las etiquetas que cuelgan de dos rollos, con unos trazos de letra.
        listOf(bocas[3] to -6f, bocas[2] to 5f).forEachIndexed { i, (c, giro) ->
            val o = c + Offset(r * (if (i == 0) -0.5f else 0.3f), r * 0.2f)
            rotate(giro, o) {
                val e = Rect(o.x - r * 0.3f, o.y + r * 0.5f, o.x + r * 0.3f, o.y + r * 1.45f)
                drawLine(a.luz(CUERO, cerca), o - Offset(0f, r * 0.1f), Offset(o.x, e.top + r * 0.1f), 0.6f * u)
                drawRect(a.sombra(0.3f), e.topLeft + Offset(1f * u, 1.2f * u), e.size)
                drawRect(a.luz(HUESO, cerca + 0.1f), e.topLeft, e.size)
                for (j in 0 until 3) drawLine(a.luz(BARNIZ, cerca).copy(alpha = 0.7f), Offset(e.left + r * 0.08f, e.top + r * (0.3f + 0.2f * j)), Offset(e.right - r * (0.08f + 0.1f * (j % 2)), e.top + r * (0.3f + 0.2f * j)), 0.6f * u)
            }
        }
    }

    /** La lechuza de terracota: el cuerpo de arcilla con las plumas y las alas en barniz negro y los ojos grandes; en los Sueños se le encienden. */
    private fun DrawScope.lechuza(pie: Offset, alto: Float, a: Aire, cerca: Float, haciaX: Float, caida: Offset, despierta: Boolean) {
        val u = density
        val cx = pie.x
        val cuerpo = perfil(cx, pie.y, alto, floatArrayOf(0.17f, -1.0f, 0.3f, -0.93f, 0.35f, -0.76f, 0.33f, -0.6f, 0.37f, -0.42f, 0.36f, -0.2f, 0.27f, -0.05f, 0.24f, 0f))
        translate(caida.x, caida.y) { drawPath(cuerpo, a.sombra(if (a.oscuro) 0.4f else 0.2f)) }
        contacto(Offset(cx + 2f * u, pie.y), alto * 0.6f, alto * 0.07f, a)
        // Pintada con barniz diluido, pardo, como las lechuzas de los vasos áticos: no con negro macizo.
        val barniz = a.luz(mezcla(BARNIZ, TERRACOTA, 0.22f), cerca)
        val arcilla = a.luz(mezcla(TERRACOTA, PAPIRO, 0.45f), cerca)
        drawPath(cuerpo, a.luz(TERRACOTA, cerca))
        clipPath(cuerpo) {
            // El pecho, de arcilla más clara, moteado de plumas.
            drawOval(arcilla, Offset(cx - alto * 0.19f, pie.y - alto * 0.62f), Size(alto * 0.38f, alto * 0.66f))
            for (f in 0 until 5) for (j in -1..1) {
                val o = Offset(cx + j * alto * 0.085f + (f % 2 - 0.5f) * alto * 0.05f, pie.y - alto * (0.53f - 0.095f * f))
                drawPath(poligono(o.x - alto * 0.017f, o.y, o.x + alto * 0.017f, o.y, o.x, o.y + alto * 0.042f), barniz)
            }
            // Las alas plegadas, a los lados, con sus plumas largas.
            for (l in listOf(-1f, 1f)) {
                val ala = Path().apply {
                    moveTo(cx + l * alto * 0.42f, pie.y - alto * 0.66f)
                    cubicTo(cx + l * alto * 0.2f, pie.y - alto * 0.6f, cx + l * alto * 0.16f, pie.y - alto * 0.24f, cx + l * alto * 0.27f, pie.y - alto * 0.06f)
                    lineTo(cx + l * alto * 0.5f, pie.y - alto * 0.06f); close()
                }
                drawPath(ala, a.luz(mezcla(TERRACOTA, BARNIZ, 0.5f), cerca))
                for (j in 0 until 4) drawLine(barniz, Offset(cx + l * alto * (0.2f + 0.03f * j), pie.y - alto * (0.5f - 0.1f * j)), Offset(cx + l * alto * 0.42f, pie.y - alto * (0.4f - 0.1f * j)), 0.9f * u)
            }
            drawRect(barniz, Offset(cx - alto, pie.y - alto * 0.05f), Size(alto * 2, alto * 0.055f))
            // La cabeza: las plumas de la frente en puntos.
            for (j in -3..3) drawCircle(barniz, alto * 0.014f, Offset(cx + j * alto * 0.05f, pie.y - alto * (0.955f - 0.006f * j * j)))
        }
        redondo(cuerpo, a, cerca, haciaX, brillo = 0.18f)
        // Los ojos: el disco de plumas con sus rayos y la pupila. Despierta, el iris de oro que alumbra.
        for (l in listOf(-1f, 1f)) {
            val o = Offset(cx + l * alto * 0.135f, pie.y - alto * 0.77f)
            drawCircle(arcilla, alto * 0.125f, o)
            drawCircle(barniz, alto * 0.125f, o, style = Stroke(1.1f * u))
            for (j in 0 until 10) {
                val ang = 6.283f * j / 10
                drawLine(barniz.copy(alpha = 0.7f), o + Offset(cos(ang), sin(ang)) * (alto * 0.075f), o + Offset(cos(ang), sin(ang)) * (alto * 0.115f), 0.6f * u)
            }
            drawCircle(if (despierta) OJO else a.luz(mezcla(BARNIZ, TERRACOTA, 0.1f), cerca), alto * 0.062f, o)
            if (despierta) drawCircle(BARNIZ, alto * 0.03f, o)
            drawCircle(Color.White.copy(alpha = if (despierta) 0.9f else 0.5f), alto * 0.015f, o - Offset(alto * 0.02f, alto * 0.024f))
        }
        drawPath(poligono(cx - alto * 0.035f, pie.y - alto * 0.72f, cx + alto * 0.035f, pie.y - alto * 0.72f, cx, pie.y - alto * 0.6f), barniz)
        if (despierta) mancha(Offset(cx, pie.y - alto * 0.77f), alto * 0.46f, OJO.copy(alpha = 0.34f))
    }

    /** El contorno de un lécito de pie en [pie], de alto [h]. */
    private fun lecitoForma(pie: Offset, h: Float): Path =
        perfil(pie.x, pie.y, h, floatArrayOf(0.11f, -1.0f, 0.115f, -0.93f, 0.045f, -0.89f, 0.04f, -0.78f, 0.075f, -0.745f, 0.165f, -0.7f, 0.18f, -0.64f, 0.18f, -0.22f, 0.15f, -0.09f, 0.07f, -0.05f, 0.125f, -0.035f, 0.13f, 0f))

    /** El lécito de fondo blanco: la boca, el cuello y el pie de barniz negro, el hombro con sus rayos y el cuerpo blanco con un dibujo fino. */
    private fun DrawScope.lecito(pie: Offset, h: Float, a: Aire, cerca: Float, haciaX: Float) {
        val u = density
        val cx = pie.x
        val forma = lecitoForma(pie, h)
        val barniz = a.luz(BARNIZ, cerca)
        val asa = Path().apply {
            moveTo(cx + h * 0.04f, pie.y - h * 0.88f)
            cubicTo(cx + h * 0.2f, pie.y - h * 0.92f, cx + h * 0.2f, pie.y - h * 0.74f, cx + h * 0.14f, pie.y - h * 0.7f)
        }
        drawPath(asa, barniz, style = Stroke(h * 0.03f, cap = StrokeCap.Round))
        drawPath(forma, a.luz(mezcla(HUESO, Color.White, 0.35f), cerca))
        clipPath(forma) {
            drawRect(barniz, Offset(cx - h, pie.y - h * 1.05f), Size(h * 2, h * 0.31f))
            drawRect(barniz, Offset(cx - h, pie.y - h * 0.105f), Size(h * 2, h * 0.11f))
            // El hombro reservado, con sus rayos, y la greca bajo él.
            drawRect(a.luz(mezcla(TERRACOTA, PAPIRO, 0.45f), cerca), Offset(cx - h, pie.y - h * 0.74f), Size(h * 2, h * 0.075f))
            for (j in -4..4) drawLine(barniz, Offset(cx + j * h * 0.035f, pie.y - h * 0.735f), Offset(cx + j * h * 0.042f, pie.y - h * 0.675f), 0.7f * u)
            drawRect(barniz, Offset(cx - h, pie.y - h * 0.665f), Size(h * 2, 0.9f * u))
            greca(Rect(cx - h * 0.2f, pie.y - h * 0.645f, cx + h * 0.2f, pie.y - h * 0.612f), barniz.copy(alpha = 0.8f))
            drawRect(barniz, Offset(cx - h, pie.y - h * 0.6f), Size(h * 2, 0.8f * u))
            drawRect(a.luz(ROJO, cerca), Offset(cx - h, pie.y - h * 0.24f), Size(h * 2, 0.9f * u))
            // El dibujo del cuerpo: unas líneas finas, casi borradas.
            val trazo = a.luz(mezcla(ROJO, ZOCALO, 0.4f), cerca).copy(alpha = 0.6f)
            drawPath(Path().apply {
                moveTo(cx - h * 0.07f, pie.y - h * 0.28f); cubicTo(cx - h * 0.1f, pie.y - h * 0.4f, cx - h * 0.02f, pie.y - h * 0.46f, cx - h * 0.03f, pie.y - h * 0.54f)
                moveTo(cx + h * 0.02f, pie.y - h * 0.28f); cubicTo(cx + h * 0.08f, pie.y - h * 0.38f, cx + h * 0.03f, pie.y - h * 0.46f, cx + h * 0.02f, pie.y - h * 0.5f)
            }, trazo, style = Stroke(0.6f * u))
            drawCircle(trazo, h * 0.022f, Offset(cx - h * 0.005f, pie.y - h * 0.555f), style = Stroke(0.6f * u))
        }
        redondo(forma, a, cerca, haciaX, brillo = 0.25f)
    }

    /** La rama de olivo fresca metida en el lécito: tres ramillas con sus hojas de dos caras (verde arriba, plata abajo) y unas aceitunas. */
    private fun DrawScope.ramaOlivo(boca: Offset, w: Float, a: Aire, cerca: Float, caida: Offset) {
        val u = density
        val ramas = listOf(
            listOf(boca, boca + Offset(-w * 0.01f, -w * 0.07f), boca + Offset(-w * 0.07f, -w * 0.12f), boca + Offset(-w * 0.1f, -w * 0.185f)),
            listOf(boca, boca + Offset(w * 0.005f, -w * 0.09f), boca + Offset(w * 0.03f, -w * 0.16f), boca + Offset(w * 0.02f, -w * 0.245f)),
            listOf(boca, boca + Offset(w * 0.02f, -w * 0.05f), boca + Offset(w * 0.07f, -w * 0.08f), boca + Offset(w * 0.115f, -w * 0.15f)),
        )
        fun dibuja(soloSombra: Boolean) {
            ramas.forEachIndexed { i, q ->
                val tallo = Path().apply { moveTo(q[0].x, q[0].y); cubicTo(q[1].x, q[1].y, q[2].x, q[2].y, q[3].x, q[3].y) }
                drawPath(tallo, if (soloSombra) a.sombra(if (a.oscuro) 0.36f else 0.18f) else a.luz(mezcla(OLIVO, CUERO, 0.55f), cerca), style = Stroke(1.3f * u, cap = StrokeCap.Round))
                val hojas = 9 + i
                for (k in 1..hojas) {
                    val t = k / hojas.toFloat()
                    val p = bezier(q[0], q[1], q[2], q[3], 0.18f + 0.82f * t)
                    val p2 = bezier(q[0], q[1], q[2], q[3], 0.18f + 0.82f * t - 0.04f)
                    val dir = Math.toDegrees(kotlin.math.atan2((p.y - p2.y).toDouble(), (p.x - p2.x).toDouble())).toFloat()
                    val lado = if (k % 2 == 0) -1f else 1f
                    val giro = dir + 90f + lado * (52f + 26f * azar(821 + i, k)) + (if (k == hojas) -lado * 50f else 0f)
                    val largo = w * (0.036f + 0.014f * azar(822 + i, k))
                    val color = when {
                        soloSombra -> a.sombra(if (a.oscuro) 0.36f else 0.18f)
                        azar(823 + i, k) < 0.3f -> a.luz(mezcla(OLIVO, HUESO, 0.45f), cerca)
                        azar(823 + i, k) < 0.6f -> a.luz(mezcla(OLIVO, ZOCALO, 0.3f), cerca)
                        else -> a.luz(OLIVO, cerca)
                    }
                    rotate(giro, p) {
                        drawOval(color, Offset(p.x - largo * 0.15f, p.y - largo), Size(largo * 0.3f, largo))
                        if (!soloSombra) drawLine(a.claro(0.3f), Offset(p.x - largo * 0.03f, p.y - largo * 0.86f), Offset(p.x - largo * 0.03f, p.y - largo * 0.12f), 0.4f * u)
                    }
                }
                if (!soloSombra) for (k in 0 until 2) {
                    val p = bezier(q[0], q[1], q[2], q[3], 0.4f + 0.3f * k) + Offset(w * 0.006f, w * 0.006f)
                    drawOval(a.luz(mezcla(OLIVO, ZOCALO, 0.55f), cerca), Offset(p.x - w * 0.005f, p.y - w * 0.007f), Size(w * 0.01f, w * 0.014f))
                    drawCircle(a.claro(0.4f), w * 0.0018f, p - Offset(w * 0.0015f, w * 0.003f))
                }
            }
        }
        translate(caida.x, caida.y) { dibuja(true) }
        dibuja(false)
    }

    /** Lo de escribir, bajo el nicho: el tintero de barniz negro con su cálamo, el platito del agua con la esponja y unas tabas sueltas. */
    private fun DrawScope.utiles(apoyo: Float, a: Aire, cerca: (Offset) -> Float, caida: (Offset) -> Offset) {
        val u = density; val w = size.width
        // El tintero y el cálamo de caña, metido de punta.
        val pt = Offset(w * 0.098f, apoyo)
        val kt = cerca(pt)
        val s = w * 0.058f
        val tintero = perfil(pt.x, pt.y, s, floatArrayOf(0.26f, -0.8f, 0.3f, -0.74f, 0.46f, -0.6f, 0.5f, -0.3f, 0.42f, -0.06f, 0.44f, 0f))
        translate(caida(pt).x, caida(pt).y) { drawPath(tintero, a.sombra(if (a.oscuro) 0.4f else 0.2f)) }
        contacto(Offset(pt.x + 2f * u, pt.y), s * 1.1f, s * 0.14f, a)
        val c0 = Offset(pt.x + s * 0.06f, pt.y - s * 0.7f); val c1 = Offset(pt.x + s * 0.95f, pt.y - s * 2.1f)
        drawLine(a.sombra(if (a.oscuro) 0.36f else 0.18f), c0 + caida(pt) * 1.4f, c1 + caida(pt) * 1.4f, 1.6f * u, StrokeCap.Round)
        drawLine(a.luz(mezcla(PAPIRO, MIMBRE, 0.5f), kt), c0, c1, 2f * u, StrokeCap.Round)
        drawLine(a.claro(0.5f), c0 - Offset(0.6f * u, 0f), c1 - Offset(0.6f * u, 0f), 0.6f * u)
        drawLine(a.luz(CUERO, kt), c0 + (c1 - c0) * 0.55f, c0 + (c1 - c0) * 0.6f, 2.2f * u)
        drawPath(tintero, a.luz(BARNIZ, kt))
        clipPath(tintero) { drawRect(a.luz(TERRACOTA, kt), Offset(pt.x - s, pt.y - s * 0.2f), Size(s * 2, 1f * u)) }
        redondo(tintero, a, kt, 1f, brillo = 0.34f)
        drawOval(a.luz(mezcla(BARNIZ, TERRACOTA, 0.3f), kt), Offset(pt.x - s * 0.27f, pt.y - s * 0.86f), Size(s * 0.54f, s * 0.14f))
        drawOval(a.luz(BARNIZ, kt), Offset(pt.x - s * 0.19f, pt.y - s * 0.83f), Size(s * 0.38f, s * 0.08f))
        // El platito con agua y la esponja para alisar la cera.
        val pp = Offset(w * 0.198f, apoyo)
        val kp = cerca(pp)
        val an = w * 0.086f
        val plato = perfil(pp.x, pp.y, an, floatArrayOf(0.5f, -0.2f, 0.44f, -0.1f, 0.2f, -0.03f, 0.16f, 0f))
        contacto(Offset(pp.x + 2f * u, pp.y), an * 1.1f, an * 0.1f, a)
        drawPath(plato, a.luz(BARNIZ, kp))
        redondo(plato, a, kp, -1f, brillo = 0.3f)
        drawOval(a.luz(mezcla(BARNIZ, TERRACOTA, 0.5f), kp), Offset(pp.x - an * 0.5f, pp.y - an * 0.245f), Size(an, an * 0.09f))
        drawOval(a.luz(mezcla(EGEO, HUESO, 0.45f), kp), Offset(pp.x - an * 0.42f, pp.y - an * 0.225f), Size(an * 0.84f, an * 0.055f))
        val esponja = Path()
        val ce = Offset(pp.x + an * 0.14f, pp.y - an * 0.33f)
        for (k in 0 until 10) {
            val ang = 6.283f * k / 10
            val rr = an * (0.2f + 0.05f * azar(831, k))
            val q = Offset(ce.x + rr * cos(ang) * 1.2f, ce.y + rr * sin(ang) * 0.72f)
            if (k == 0) esponja.moveTo(q.x, q.y) else esponja.lineTo(q.x, q.y)
        }
        esponja.close()
        drawPath(esponja, a.luz(mezcla(PAPIRO, MIMBRE, 0.45f), kp))
        clipPath(esponja) { for (k in 0 until 9) drawCircle(a.sombra(0.34f), an * (0.012f + 0.016f * azar(832, k)), Offset(ce.x + an * (azar(833, k) - 0.5f) * 0.42f, ce.y + an * (azar(834, k) - 0.5f) * 0.24f)) }
        drawPath(borde(esponja, -an * 0.05f, -an * 0.05f), a.sombra(0.26f))
        drawPath(borde(esponja, an * 0.04f, an * 0.04f), a.claro(0.4f))
        // Tres tabas (astrágalos) que alguien dejó tiradas.
        listOf(Offset(w * 0.262f, apoyo - 1.5f * u), Offset(w * 0.553f, apoyo + 1f * u), Offset(w * 0.578f, apoyo - 1f * u)).forEachIndexed { i, o ->
            val kk = cerca(o)
            drawOval(a.sombra(0.34f), Offset(o.x - 2.4f * u, o.y - 0.4f * u), Size(6.4f * u, 2.6f * u))
            rotate(-20f + 35f * i, o) {
                drawRoundRect(a.luz(mezcla(HUESO, PAPIRO, 0.4f), kk), Offset(o.x - 2.8f * u, o.y - 3.4f * u), Size(5.6f * u, 3.6f * u), CornerRadius(1.5f * u))
                drawLine(a.sombra(0.4f), Offset(o.x - 1.2f * u, o.y - 1.6f * u), Offset(o.x + 1.2f * u, o.y - 1.6f * u), 0.9f * u, StrokeCap.Round)
                drawLine(a.claro(0.6f), Offset(o.x - 2f * u, o.y - 3f * u), Offset(o.x + 1.6f * u, o.y - 3f * u), 0.6f * u)
            }
        }
    }

    // ---------------------------------------------------------------- lo que hay al pie

    /** El asiento plegable (díphros okladías), de frente: las dos patas en aspa con sus garras, el perno, el cuero del asiento y, encima, el manto doblado. */
    private fun DrawScope.asiento(pie: Offset, an: Float, alto: Float, a: Aire, cerca: Float, haciaX: Float) {
        val u = density
        val cx = pie.x
        val arriba = pie.y - alto
        contacto(Offset(cx + an * 0.12f, pie.y), an * 1.3f, alto * 0.1f, a, 0.4f)
        fun pata(l: Float, atras: Boolean) = Path().apply {
            val dx = if (atras) an * 0.05f else 0f; val dy = if (atras) -alto * 0.06f else 0f
            moveTo(cx + l * an * 0.46f + dx, arriba + dy)
            cubicTo(cx + l * an * 0.34f + dx, arriba + alto * 0.36f + dy, cx - l * an * 0.2f + dx, arriba + alto * 0.6f + dy, cx - l * an * 0.4f + dx, pie.y - alto * 0.08f + dy)
            lineTo(cx - l * an * 0.5f + dx, pie.y + dy)
        }
        val grueso = an * 0.062f
        for (l in listOf(-1f, 1f)) drawPath(pata(l, true), a.luz(mezcla(MADERA_OSC, a.hondo, 0.2f), cerca), style = Stroke(grueso, cap = StrokeCap.Round, join = StrokeJoin.Round))
        for (l in listOf(1f, -1f)) {
            val p = pata(l, false)
            drawPath(p, a.luz(mezcla(MADERA, MADERA_OSC, 0.35f), cerca), style = Stroke(grueso, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(corrido(p, -grueso * 0.28f, -grueso * 0.1f), a.claro(0.35f), style = Stroke(grueso * 0.2f, cap = StrokeCap.Round))
            drawPath(corrido(p, grueso * 0.3f, grueso * 0.1f), a.sombra(0.3f), style = Stroke(grueso * 0.22f, cap = StrokeCap.Round))
            // La garra con que pisa.
            val g = Offset(cx - l * an * 0.5f, pie.y)
            drawOval(a.luz(mezcla(MADERA, MADERA_OSC, 0.5f), cerca), Offset(g.x - grueso * 1.1f, g.y - grueso * 0.8f), Size(grueso * 2.2f, grueso * 1.1f))
        }
        val perno = Offset(cx, arriba + alto * 0.52f)
        drawCircle(a.sombra(0.4f), grueso * 0.5f, perno + Offset(0.6f * u, 0.8f * u))
        drawCircle(a.luz(BRONCE, cerca), grueso * 0.42f, perno)
        drawCircle(a.claro(0.5f), grueso * 0.14f, perno - Offset(grueso * 0.12f, grueso * 0.12f))
        // El travesaño y el cuero del asiento.
        val asiento = Rect(cx - an * 0.5f, arriba - alto * 0.05f, cx + an * 0.52f, arriba + alto * 0.05f)
        drawRoundRect(a.luz(CUERO, cerca), asiento.topLeft, asiento.size, CornerRadius(asiento.height * 0.4f))
        drawRect(a.claro(0.25f), Offset(asiento.left + 2f * u, asiento.top), Size(asiento.width - 4f * u, 1f * u))
        // El manto de lana, doblado en cuatro, con sus orillas rojas; una punta cuelga por la derecha.
        val lana = a.luz(HUESO, cerca + 0.06f)
        val orilla = a.luz(ROJO, cerca + 0.06f)
        val punta = Path().apply {
            moveTo(cx + an * 0.2f, asiento.top - alto * 0.04f); lineTo(cx + an * 0.52f, asiento.top - alto * 0.02f)
            cubicTo(cx + an * 0.56f, asiento.top + alto * 0.2f, cx + an * 0.5f, asiento.top + alto * 0.36f, cx + an * 0.54f, asiento.top + alto * 0.5f)
            lineTo(cx + an * 0.38f, asiento.top + alto * 0.42f); lineTo(cx + an * 0.3f, asiento.top + alto * 0.46f); lineTo(cx + an * 0.24f, asiento.top + alto * 0.3f)
            close()
        }
        drawPath(corrido(punta, 2f * u, 1.5f * u), a.sombra(0.3f))
        drawPath(punta, mezcla(lana, a.hondo, 0.1f))
        clipPath(punta) {
            for (j in 0 until 3) drawLine(a.sombra(0.2f), Offset(cx + an * (0.3f + 0.08f * j), asiento.top), Offset(cx + an * (0.28f + 0.09f * j), asiento.top + alto * 0.5f), 0.8f * u)
            drawLine(orilla, Offset(cx + an * 0.24f, asiento.top + alto * 0.27f), Offset(cx + an * 0.56f, asiento.top + alto * 0.43f), 2f * u)
        }
        for (j in 0 until 3) {
            val y1 = asiento.top - j * alto * 0.085f; val y0 = y1 - alto * 0.095f
            val desc = j * an * 0.02f
            val capa = Path().apply { addRoundRect(RoundRect(Rect(cx - an * 0.46f + desc, y0, cx + an * 0.5f - desc * 0.5f, y1), CornerRadius(alto * 0.045f))) }
            drawPath(capa, mezcla(lana, a.hondo, 0.06f * (2 - j)))
            drawPath(borde(capa, 0f, -alto * 0.03f), a.sombra(0.22f))
            clipPath(capa) {
                drawRect(orilla, Offset(cx - an * 0.46f + desc + an * 0.035f, y0), Size(an * 0.022f, y1 - y0))
                drawRect(orilla, Offset(cx - an * 0.46f + desc + an * 0.07f, y0), Size(an * 0.008f, y1 - y0))
                drawRect(a.sombra(0.16f), Offset(cx + an * 0.26f, y0), Size(an * 0.3f, y1 - y0))
            }
            if (j == 2) drawPath(borde(capa, 0f, alto * 0.02f), a.claro(0.55f))
        }
        if (a.oscuro) drawLine(FILO_MUEBLE.copy(alpha = 0.5f * cerca), Offset(cx - an * 0.4f, asiento.top - alto * 0.29f), Offset(cx + an * 0.44f, asiento.top - alto * 0.29f), 1f * u)
    }

    /** Un par de sandalias dejadas bajo el mueble: las suelas de cuero, una montada en la otra, con sus correas. */
    private fun DrawScope.sandalias(pie: Offset, largo: Float, a: Aire, cerca: Float) {
        val u = density
        listOf(Offset(largo * 0.42f, -largo * 0.1f) to -6f, Offset(0f, 0f) to 5f).forEach { (dd, giro) ->
            val o = pie + dd
            val alto = largo * 0.26f
            rotate(giro, o) {
                // La suela, vista desde arriba y de lado: la planta ancha delante y el talón más estrecho.
                val suela = Path().apply {
                    op(ovalo(Offset(o.x - largo * 0.16f, o.y), largo * 0.33f, alto * 0.5f), ovalo(Offset(o.x + largo * 0.26f, o.y + alto * 0.03f), largo * 0.23f, alto * 0.4f), PathOperation.Union)
                }
                drawPath(corrido(suela, 2.4f * u, 2.6f * u), a.sombra(0.36f))
                drawPath(corrido(suela, 0f, 1.8f * u), a.luz(mezcla(CUERO, BARNIZ, 0.3f), cerca))
                drawPath(suela, a.luz(mezcla(CUERO, MIMBRE, 0.5f), cerca))
                drawPath(borde(suela, largo * 0.03f, alto * 0.14f), a.claro(0.3f))
                // Las correas: la del empeine y la del talón, de pie, y la tira que las une por arriba.
                val correa = a.luz(mezcla(CUERO, ROJO, 0.3f), cerca)
                val empeine = Rect(o.x - largo * 0.26f, o.y - alto * 1.25f, o.x - largo * 0.02f, o.y + alto * 0.3f)
                val talon = Rect(o.x + largo * 0.2f, o.y - alto * 1.7f, o.x + largo * 0.44f, o.y + alto * 0.3f)
                drawArc(a.sombra(0.3f), 180f, 180f, false, empeine.topLeft + Offset(2f * u, 1.4f * u), empeine.size, style = Stroke(1.6f * u))
                drawArc(correa, 180f, 180f, false, empeine.topLeft, empeine.size, style = Stroke(1.8f * u))
                drawArc(correa, 180f, 180f, false, talon.topLeft, talon.size, style = Stroke(1.8f * u))
                drawLine(correa, Offset(empeine.center.x, empeine.top), Offset(talon.center.x, talon.top), 1.4f * u)
                drawLine(correa, Offset(empeine.center.x, empeine.top), Offset(o.x - largo * 0.4f, o.y - alto * 0.1f), 1.4f * u)
                drawArc(a.claro(0.35f), 200f, 80f, false, talon.topLeft, talon.size, style = Stroke(0.6f * u))
            }
        }
    }

    /** El cesto grande de mimbre, que se sale por el borde, con los rollos de papiro de pie dentro. */
    private fun DrawScope.cestoRollos(pie: Offset, an: Float, alto: Float, a: Aire, cerca: Float, haciaX: Float) {
        val u = density
        val cx = pie.x
        val boca = pie.y - alto
        val cuerpo = Path().apply {
            moveTo(cx - an * 0.5f, boca)
            cubicTo(cx - an * 0.56f, boca + alto * 0.5f, cx - an * 0.48f, pie.y - alto * 0.1f, cx - an * 0.36f, pie.y)
            lineTo(cx + an * 0.36f, pie.y)
            cubicTo(cx + an * 0.48f, pie.y - alto * 0.1f, cx + an * 0.56f, boca + alto * 0.5f, cx + an * 0.5f, boca)
            close()
        }
        drawPath(corrido(cuerpo, an * 0.06f, alto * 0.03f), a.sombra(if (a.oscuro) 0.4f else 0.24f))
        contacto(Offset(cx + an * 0.1f, pie.y), an * 1.1f, alto * 0.12f, a, 0.4f)
        // Los rollos, de atrás adelante: cada uno un cilindro con su boca arriba, alguno en su funda teñida.
        val rollos = listOf(Triple(0.3f, 0.5f, 9f), Triple(-0.02f, 0.62f, 3f), Triple(-0.3f, 0.4f, -8f), Triple(0.12f, 0.3f, -3f))
        rollos.forEachIndexed { i, (fx, sube, giro) ->
            val base = Offset(cx + fx * an, boca + alto * 0.3f)
            val rr = an * 0.085f
            val largo = alto * (0.3f + sube)
            rotate(giro, base) {
                val r = Rect(base.x - rr, base.y - largo, base.x + rr, base.y)
                val tono = a.luz(when (i) { 1 -> mezcla(ROJO, TERRACOTA, 0.3f); 3 -> mezcla(EGEO, PANO, 0.5f); else -> PAPIRO }, cerca)
                drawRect(a.luz(PAPIRO, cerca), r.topLeft, r.size)
                if (i == 1 || i == 3) drawRect(tono, Offset(r.left, r.top + rr * 0.9f), Size(r.width, r.height))
                drawRect(a.claro(0.3f), Offset(r.left + r.width * 0.14f, r.top), Size(r.width * 0.16f, r.height))
                drawRect(a.sombra(0.2f), Offset(r.left + r.width * 0.62f, r.top), Size(r.width * 0.38f, r.height))
                drawRect(a.sombra(0.2f), Offset(r.left + r.width * 0.84f, r.top), Size(r.width * 0.16f, r.height))
                drawOval(a.luz(mezcla(PAPIRO, HUESO, 0.45f), cerca), Offset(r.left, r.top - rr * 0.34f), Size(r.width, rr * 0.68f))
                for (j in 1..2) drawOval(a.luz(mezcla(PAPIRO, CUERO, 0.55f), cerca), Offset(r.left + rr * 0.3f * j, r.top - rr * (0.34f - 0.1f * j)), Size(r.width - rr * 0.6f * j, rr * (0.68f - 0.2f * j)), style = Stroke(0.6f * u))
                drawRect(a.luz(MADERA_OSC, cerca), Offset(base.x - rr * 0.14f, r.top - rr * 0.6f), Size(rr * 0.28f, rr * 0.6f))
                drawCircle(a.luz(mezcla(MADERA_OSC, BRONCE, 0.4f), cerca), rr * 0.24f, Offset(base.x, r.top - rr * 0.66f))
                if (i == 1) {
                    val e = Rect(base.x - rr * 0.6f, r.top + rr * 1.3f, base.x + rr * 0.5f, r.top + rr * 3f)
                    drawLine(a.luz(HUESO, cerca), Offset(base.x, r.top - rr * 0.2f), Offset(e.center.x, e.top), 0.6f * u)
                    drawRect(a.sombra(0.3f), e.topLeft + Offset(1f * u, 1f * u), e.size)
                    drawRect(a.luz(HUESO, cerca + 0.08f), e.topLeft, e.size)
                    for (j in 0 until 3) drawLine(a.luz(BARNIZ, cerca).copy(alpha = 0.7f), Offset(e.left + rr * 0.14f, e.top + rr * (0.4f + 0.4f * j)), Offset(e.right - rr * 0.16f, e.top + rr * (0.4f + 0.4f * j)), 0.6f * u)
                }
            }
        }
        // El cesto: el mimbre tejido en hiladas, con sus montantes, y el borde trenzado.
        val mimbre = a.luz(MIMBRE, cerca)
        drawPath(cuerpo, mimbre)
        clipPath(cuerpo) {
            val hiladas = 9
            for (j in 0 until hiladas) {
                val y = boca + alto * (j + 0.5f) / hiladas
                drawLine(a.sombra(0.3f), Offset(cx - an, y + alto * 0.05f), Offset(cx + an, y + alto * 0.05f), 0.9f * u)
                drawLine(a.claro(0.25f), Offset(cx - an, y - alto * 0.035f), Offset(cx + an, y - alto * 0.035f), 0.7f * u)
                var x = cx - an * 0.6f + (j % 2) * an * 0.035f
                while (x < cx + an * 0.6f) {
                    drawLine(a.luz(mezcla(MIMBRE, CUERO, 0.55f), cerca), Offset(x, y - alto * 0.04f), Offset(x + an * 0.006f, y + alto * 0.05f), 1.2f * u)
                    x += an * 0.07f
                }
            }
            drawRect(a.luz(mezcla(ROJO, CUERO, 0.4f), cerca), Offset(cx - an, boca + alto * 0.42f), Size(an * 2, alto * 0.1f))
        }
        redondo(cuerpo, a, cerca, haciaX, fuerza = 1.2f, claro = 0.4f)
        val trenza = Path().apply { addRoundRect(RoundRect(Rect(cx - an * 0.53f, boca - alto * 0.05f, cx + an * 0.53f, boca + alto * 0.05f), CornerRadius(alto * 0.05f))) }
        drawPath(trenza, a.luz(mezcla(MIMBRE, CUERO, 0.3f), cerca))
        clipPath(trenza) {
            var x = cx - an * 0.56f
            while (x < cx + an * 0.56f) { drawLine(a.sombra(0.34f), Offset(x, boca + alto * 0.05f), Offset(x + an * 0.04f, boca - alto * 0.05f), 1f * u); x += an * 0.045f }
        }
        drawPath(borde(trenza, 0f, alto * 0.025f), a.claro(0.4f))
        if (a.oscuro) drawPath(borde(trenza, 0f, alto * 0.02f), FILO_MUEBLE.copy(alpha = 0.5f * cerca))
    }

    // ---------------------------------------------------------------- lo que se deja en los huecos libres

    /**
     * Lo del tema en lo que queda libre de la repisa [k], a la derecha de [oc] (hasta donde llegan los volúmenes): según cuánto
     * hueco haya, una tablilla apoyada en el último volumen, la pila de tablillas atadas con una lamparita encima, el cesto de
     * los óstraka, el estilo y la espátula, un lécito tumbado. Cada repisa escoge lo suyo, para que no se repita.
     */
    private fun DrawScope.huecoLibre(k: Int, r: Rect, oc: Float, a: Aire, cerca: Float, haciaX: Float) {
        val u = density
        val libre = (r.right - oc) / u
        val y = r.bottom - 1.6f * u
        when {
            libre >= 150f -> if (k % 2 == 1) {
                tablillaApoyada(oc + 1f * u, y, a, cerca + 0.04f, k)
                estiloYEspatula(Offset(oc + 62f * u, y), a, cerca, conEspatula = true)
                cestoOstraka(Offset(r.right - 48f * u, y), 62f * u, 40f * u, a, cerca - 0.03f, haciaX, k)
            } else {
                pilaTablillas(Offset(oc + 8f * u, y), 60f * u, 4, a, cerca + 0.03f, k)
                lamparita(Offset(oc + 36f * u, y - 4 * 8f * u), 30f * u, a, cerca + 0.03f)
                lecitoTumbado(Offset(r.right - 92f * u, y), 76f * u, a, cerca - 0.03f)
            }
            libre >= 74f -> when (k % 3) {
                0 -> { pilaTablillas(Offset(oc + 7f * u, y), 58f * u, 3, a, cerca, k); lamparita(Offset(oc + 40f * u, y - 3 * 8f * u), 30f * u, a, cerca) }
                1 -> cestoOstraka(Offset((oc + r.right) / 2 + 4f * u, y), 56f * u, 40f * u, a, cerca, haciaX, k)
                else -> { tablillaApoyada(oc + 1f * u, y, a, cerca, k); if (libre >= 84f) lamparita(Offset(oc + 64f * u, y), 30f * u, a, cerca) }
            }
            libre >= 46f -> when (k % 3) {
                0 -> tablillaApoyada(oc + 1f * u, y, a, cerca, k)
                1 -> lamparita(Offset((oc + r.right) / 2, y), 30f * u, a, cerca)
                else -> estiloYEspatula(Offset(oc + 10f * u, y), a, cerca, conEspatula = true)
            }
            libre >= 26f -> when {
                k % 2 == 1 && libre >= 36f -> lamparita(Offset((oc + r.right) / 2 - 1f * u, y), 28f * u, a, cerca)
                k % 4 == 2 -> {
                    // Un lécito chico, de pie, arrimado al último volumen.
                    val pie = Offset(oc + 13f * u, y)
                    translate(if (a.oscuro) 9f * u else 6f * u, 2f * u) { drawPath(lecitoForma(pie, 44f * u), a.sombra(if (a.oscuro) 0.38f else 0.24f)) }
                    contacto(Offset(pie.x + 2f * u, pie.y), 15f * u, 3.4f * u, a)
                    lecito(pie, 44f * u, a, cerca, haciaX)
                }
                else -> estiloYEspatula(Offset(oc + 8f * u, y), a, cerca, conEspatula = false)
            }
        }
    }

    /** Un pinax apoyado de lado en el último volumen, haciendo de sujetalibros: pisa con una esquina y deja su cuña de sombra. */
    private fun DrawScope.tablillaApoyada(xIzq: Float, y: Float, a: Aire, cerca: Float, semilla: Int) {
        val u = density
        val an = 27f * u; val alto = 64f * u
        val giro = -13f
        val rad = Math.toRadians(13.0)
        val pivote = Offset(xIzq + an * cos(rad).toFloat() + alto * sin(rad).toFloat(), y - 1.2f * u)
        // La cuña de sombra bajo la tablilla y su sombra en el fondo.
        drawPath(poligono(xIzq, y, pivote.x, y, pivote.x - an * cos(rad).toFloat(), y - an * sin(rad).toFloat()), a.sombra(0.45f))
        rotate(giro, pivote) {
            val r = Rect(pivote.x - an, y - alto, pivote.x, y)
            drawRect(a.sombra(if (a.oscuro) 0.3f else 0.15f), r.topLeft + Offset(6f * u, 4f * u), r.size)
            val boj = a.luz(mezcla(BOJ, MADERA, 0.18f), cerca)
            drawRoundRect(mezcla(boj, a.hondo, 0.3f), r.topLeft + Offset(2.4f * u, 0f), r.size, CornerRadius(1.5f * u))
            drawRoundRect(boj, r.topLeft, r.size, CornerRadius(1.5f * u))
            drawRect(a.claro(0.24f), r.topLeft, Size(an * 0.1f, alto))
            drawRect(a.sombra(0.16f), Offset(r.right - an * 0.12f, r.top), Size(an * 0.12f, alto))
            drawRect(a.claro(0.4f), r.topLeft, Size(an, 0.9f * u))
            val ven = Rect(r.left + an * 0.16f, r.top + alto * 0.08f, r.right - an * 0.14f, r.top + alto * 0.62f)
            drawRect(a.luz(CERA, cerca), ven.topLeft, ven.size)
            drawRect(a.sombra(0.45f), ven.topLeft, Size(ven.width, 1.6f * u))
            for (j in 0 until 6) drawLine(a.inciso(cerca).copy(alpha = 0.6f), Offset(ven.left + 2f * u, ven.top + (4f + 5f * j) * u), Offset(ven.right - (2f + 5f * azar(semilla + 40, j)) * u, ven.top + (4f + 5f * j) * u), 0.7f * u)
            val cordon = a.luz(mezcla(HUESO, CUERO, 0.3f), cerca)
            drawRect(cordon, Offset(r.left, r.top + alto * 0.7f), Size(an + 2.4f * u, 5f * u))
            for (j in 1..2) drawLine(a.sombra(0.3f), Offset(r.left, r.top + alto * 0.7f + j * 1.7f * u), Offset(r.right + 2.4f * u, r.top + alto * 0.7f + j * 1.7f * u), 0.5f * u)
            if (a.oscuro) drawRect(FILO_MUEBLE.copy(alpha = 0.5f * cerca), r.topLeft, Size(0.9f * u, alto))
        }
    }

    /** Una pila de tablillas tumbadas, vistas de canto y atadas con su correa. */
    private fun DrawScope.pilaTablillas(izq: Offset, an: Float, cuantas: Int, a: Aire, cerca: Float, semilla: Int) {
        val u = density
        val g = 8f * u
        contacto(Offset(izq.x + an * 0.56f, izq.y), an * 1.15f, 5f * u, a, 0.36f)
        drawRect(a.sombra(if (a.oscuro) 0.36f else 0.22f), Offset(izq.x + 8f * u, izq.y - cuantas * g + 4f * u), Size(an, cuantas * g - 4f * u))
        for (j in 0 until cuantas) {
            val dx = (azar(semilla + 50, j) - 0.5f) * 7f * u
            val r = Rect(izq.x + dx, izq.y - (j + 1) * g, izq.x + dx + an * (0.92f + 0.08f * azar(semilla + 51, j)), izq.y - j * g)
            val boj = a.luz(mezcla(BOJ, MADERA, 0.12f + 0.2f * azar(semilla + 52, j)), cerca)
            drawRoundRect(boj, r.topLeft, r.size, CornerRadius(1.2f * u))
            drawRect(a.claro(0.4f), Offset(r.left + 1f * u, r.top), Size(r.width - 2f * u, 0.9f * u))
            drawLine(a.luz(CERA, cerca).copy(alpha = 0.75f), Offset(r.left + 1f * u, r.center.y), Offset(r.right - 1f * u, r.center.y), 0.8f * u)
            drawRect(a.sombra(0.3f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
            drawRect(a.sombra(0.16f), Offset(r.right - r.width * 0.12f, r.top), Size(r.width * 0.12f, r.height))
            if (a.oscuro && j == cuantas - 1) drawRect(FILO_MUEBLE.copy(alpha = 0.5f * cerca), r.topLeft, Size(r.width, 0.9f * u))
        }
        // La correa que las ata, con su nudo arriba.
        val xc = izq.x + an * 0.26f
        val cuero = a.luz(CUERO, cerca)
        drawRect(cuero, Offset(xc, izq.y - cuantas * g - 0.8f * u), Size(5f * u, cuantas * g + 0.8f * u))
        drawRect(a.claro(0.25f), Offset(xc, izq.y - cuantas * g - 0.8f * u), Size(1f * u, cuantas * g))
        drawCircle(cuero, 2.6f * u, Offset(xc + 2.5f * u, izq.y - cuantas * g - 1f * u))
        drawLine(cuero, Offset(xc + 2.5f * u, izq.y - cuantas * g - 1f * u), Offset(xc - 5f * u, izq.y - cuantas * g + 3f * u), 1.6f * u, StrokeCap.Round)
    }

    /** Una lamparita de barro de un pico, apagada, con su asa de anilla. */
    private fun DrawScope.lamparita(pie: Offset, an: Float, a: Aire, cerca: Float) {
        val u = density
        val alto = an * 0.36f
        contacto(Offset(pie.x + 2f * u, pie.y), an * 1.0f, alto * 0.34f, a, 0.34f)
        val barro = a.luz(mezcla(TERRACOTA, PAPIRO, 0.2f), cerca)
        val pico = poligono(pie.x + an * 0.2f, pie.y - alto * 0.8f, pie.x + an * 0.56f, pie.y - alto * 0.82f, pie.x + an * 0.58f, pie.y - alto * 0.5f, pie.x + an * 0.24f, pie.y - alto * 0.08f)
        drawPath(pico, barro)
        drawPath(pico, a.sombra(0.26f))
        drawOval(a.luz(BARNIZ, cerca), Offset(pie.x + an * 0.5f, pie.y - alto * 0.9f), Size(an * 0.09f, alto * 0.22f))
        drawArc(barro, 60f, 240f, false, Offset(pie.x - an * 0.52f, pie.y - alto * 0.8f), Size(an * 0.26f, alto * 0.56f), style = Stroke(1.8f * u))
        val cuerpo = perfil(pie.x, pie.y, an, floatArrayOf(0.12f, -0.36f, 0.26f, -0.32f, 0.33f, -0.2f, 0.28f, -0.05f, 0.18f, 0f))
        drawPath(cuerpo, barro)
        clipPath(cuerpo) { drawRect(a.luz(mezcla(TERRACOTA, BARNIZ, 0.55f), cerca), Offset(pie.x - an, pie.y - alto * 0.74f), Size(an * 2, alto * 0.16f)) }
        redondo(cuerpo, a, cerca, -1f, brillo = 0.2f)
        drawOval(a.luz(BARNIZ, cerca), Offset(pie.x - an * 0.08f, pie.y - alto * 1.06f), Size(an * 0.16f, alto * 0.14f))
    }

    /** El cesto de los óstraka: mimbre bajo y ancho, lleno de tiestos, alguno rayado. */
    private fun DrawScope.cestoOstraka(pie: Offset, an: Float, alto: Float, a: Aire, cerca: Float, haciaX: Float, semilla: Int) {
        val u = density
        val cx = pie.x
        val boca = pie.y - alto * 0.7f
        val cuerpo = Path().apply {
            moveTo(cx - an * 0.5f, boca)
            cubicTo(cx - an * 0.52f, boca + alto * 0.4f, cx - an * 0.42f, pie.y - alto * 0.06f, cx - an * 0.34f, pie.y)
            lineTo(cx + an * 0.34f, pie.y)
            cubicTo(cx + an * 0.42f, pie.y - alto * 0.06f, cx + an * 0.52f, boca + alto * 0.4f, cx + an * 0.5f, boca)
            close()
        }
        drawPath(corrido(cuerpo, if (a.oscuro) 12f * u else 8f * u, 2f * u), a.sombra(if (a.oscuro) 0.4f else 0.24f))
        contacto(Offset(cx + 3f * u, pie.y), an * 1.05f, 5f * u, a, 0.36f)
        // Los tiestos amontonados, de atrás adelante.
        for (j in 0 until 9) {
            val f = j / 8f
            val o = Offset(cx + an * (-0.36f + 0.72f * azar(semilla + 60, j)), boca - alto * (0.22f * sin(3.14f * azar(semilla + 60, j)) + 0.06f) + f * alto * 0.12f)
            tiesto(o, an * (0.1f + 0.05f * azar(semilla + 61, j)), semilla * 7 + j, a, cerca)
        }
        val mimbre = a.luz(MIMBRE, cerca)
        drawPath(cuerpo, mimbre)
        clipPath(cuerpo) {
            for (j in 0 until 6) {
                val yy = boca + alto * 0.7f * (j + 0.5f) / 6
                drawLine(a.sombra(0.3f), Offset(cx - an, yy + 2f * u), Offset(cx + an, yy + 2f * u), 0.8f * u)
                var x = cx - an * 0.56f + (j % 2) * 2.4f * u
                while (x < cx + an * 0.56f) { drawLine(a.luz(mezcla(MIMBRE, CUERO, 0.55f), cerca), Offset(x, yy - 1.6f * u), Offset(x + 0.4f * u, yy + 2f * u), 1.1f * u); x += 4.8f * u }
            }
        }
        redondo(cuerpo, a, cerca, haciaX, fuerza = 1.1f, claro = 0.4f)
        val orilla = Path().apply { addRoundRect(RoundRect(Rect(cx - an * 0.53f, boca - 2.4f * u, cx + an * 0.53f, boca + 2.4f * u), CornerRadius(2.4f * u))) }
        drawPath(orilla, a.luz(mezcla(MIMBRE, CUERO, 0.3f), cerca))
        drawPath(borde(orilla, 0f, 1.4f * u), if (a.oscuro) FILO_MUEBLE.copy(alpha = 0.5f * cerca) else a.claro(0.4f))
        // Un tiesto que se cayó fuera.
        tiesto(Offset(cx - an * 0.66f, pie.y - 2f * u), an * 0.09f, semilla * 11 + 3, a, cerca)
    }

    /** Un lécito blanco tumbado, con la boca hacia la derecha. */
    private fun DrawScope.lecitoTumbado(izq: Offset, largo: Float, a: Aire, cerca: Float) {
        val u = density
        val radio = largo * 0.18f
        contacto(Offset(izq.x + largo * 0.5f, izq.y), largo * 1.05f, 5f * u, a, 0.36f)
        drawRoundRect(a.sombra(if (a.oscuro) 0.34f else 0.2f), Offset(izq.x + 9f * u, izq.y - radio * 1.7f), Size(largo * 0.74f, radio * 1.7f), CornerRadius(radio * 0.4f))
        val pie = Offset(izq.x, izq.y - radio)
        rotate(90f, pie) { lecito(pie, largo, a, cerca, -1f) }
    }

    /** El estilo de hueso y, con [conEspatula], la espátula de bronce para alisar la cera, apoyados en el fondo. */
    private fun DrawScope.estiloYEspatula(pie: Offset, a: Aire, cerca: Float, conEspatula: Boolean) {
        val u = density
        val e0 = pie; val e1 = pie + Offset(9f * u, -58f * u)
        drawLine(a.sombra(if (a.oscuro) 0.4f else 0.26f), e0 + Offset(5f * u, 0f), e1 + Offset(8f * u, 4f * u), 1.6f * u, StrokeCap.Round)
        val hueso = a.luz(mezcla(HUESO, PAPIRO, 0.3f), cerca + 0.05f)
        drawLine(hueso, e0, e1, 2f * u, StrokeCap.Round)
        drawLine(a.claro(0.6f), e0 - Offset(0.6f * u, 0f), e1 - Offset(0.6f * u, 0f), 0.6f * u)
        // La punta abajo y, arriba, la paleta de borrar.
        drawPath(poligono(e1.x - 2.6f * u, e1.y - 6f * u, e1.x + 3.4f * u, e1.y - 5f * u, e1.x + 1.4f * u, e1.y + 2f * u, e1.x - 1f * u, e1.y + 2f * u), hueso)
        if (!conEspatula) return
        val s0 = pie + Offset(22f * u, 0f); val s1 = s0 + Offset(-7f * u, -40f * u)
        drawLine(a.sombra(if (a.oscuro) 0.4f else 0.26f), s0 + Offset(5f * u, 0f), s1 + Offset(8f * u, 3f * u), 2f * u, StrokeCap.Round)
        val bronce = a.luz(BRONCE, cerca)
        drawLine(bronce, s0 + (s1 - s0) * 0.3f, s1, 1.8f * u, StrokeCap.Round)
        drawPath(poligono(s0.x - 3.6f * u, s0.y, s0.x + 3.2f * u, s0.y, s0.x - 0.6f * u, s0.y - 14f * u, s0.x - 3.2f * u, s0.y - 13.6f * u), bronce)
        drawLine(a.claro(0.5f), s0 + Offset(-2.6f * u, -1f * u), s0 + Offset(-2.6f * u, -12f * u), 0.7f * u)
        drawCircle(bronce, 1.8f * u, s1)
    }

    // ---------------------------------------------------------------- cada entrada

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val a = Aire(oscuro, v.noche)
        val k = if (a.lumbre) cercaCuerpo(v.repisa + 0.55f, v.repisas, LADO + v.centro * (1f - 2 * LADO)) else 1f
        when (clase(v.semilla)) {
            0 -> pinax(v, medidor, a, k)
            1 -> poliptico(v, medidor, a, k)
            else -> rollo(v, medidor, a, k)
        }
    }

    /** La marca de la entrada: de día, un ramito de olivo; en los Sueños, la luna en un redondel de cera oscura. */
    private fun DrawScope.marca(c: Offset, tam: Float, luna: Boolean, trazo: Color, a: Aire, k: Float) {
        val u = density
        if (luna) {
            val fondo = a.luz(CERA, k)
            drawCircle(fondo, tam * 1.5f, c)
            drawCircle(if (a.oscuro) mezcla(LUNA, a.hondo, 0.2f - 0.2f * k) else LUNA, tam, c)
            drawCircle(fondo, tam * 0.84f, Offset(c.x + tam * 0.44f, c.y - tam * 0.26f))
        } else {
            drawLine(trazo, Offset(c.x - tam * 1.2f, c.y + tam * 0.6f), Offset(c.x + tam * 1.2f, c.y - tam * 0.6f), 0.7f * u)
            for (j in 0 until 4) {
                val f = -0.8f + 0.55f * j
                val o = Offset(c.x + tam * f, c.y - tam * f * 0.5f)
                val lado = if (j % 2 == 0) -1f else 1f
                drawLine(trazo, o, Offset(o.x + tam * 0.25f, o.y + lado * tam * 0.62f), 1.4f * u, cap = StrokeCap.Round)
            }
        }
    }

    /** El filo de la lucerna en lo que la mira: la cabeza y el costado izquierdo del volumen. */
    private fun DrawScope.filoVolumen(a: Aire, k: Float, arriba: Float, ancho: Float) {
        if (!a.oscuro || k < 0.1f) return
        val u = density
        drawRect(FILO_MUEBLE.copy(alpha = 0.6f * k), Offset(1f * u, arriba), Size(ancho - 2f * u, 1f * u))
        drawRect(FILO_MUEBLE.copy(alpha = 0.34f * k), Offset(0f, arriba + 1.5f * u), Size(0.9f * u, size.height - arriba - 1.5f * u))
    }

    /**
     * El pinax, de pie: el díptico de boj de la tapa. La hoja de delante con su marco y la ventana de cera oscura, donde van
     * incisos el día y el mes; por el canto derecho, las otras hojas (más cuanto más escribiste); lo cierra el cordón del color
     * del ánimo, en varias vueltas, con su nudo y sus cabos; abajo, el ramito de olivo marcado a fuego (en los Sueños, la luna).
     */
    private fun DrawScope.pinax(v: Volumen, medidor: TextMeasurer, a: Aire, k: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val boj = a.luz(mezcla(BOJ, listOf(MADERA, PAPIRO, TERRACOTA)[s % 3], 0.12f + 0.12f * azar(s, 3)), k)
        val hojas = 2 + (v.lleno * 3).toInt()
        val canto = minOf(w * 0.24f, hojas * 2.3f * u)
        val fr = w - canto
        // Las hojas de atrás, por el canto.
        drawRoundRect(mezcla(boj, a.hondo, 0.3f), Offset.Zero, size, CornerRadius(1.4f * u))
        for (j in 1 until hojas) {
            val x = fr + canto * j / hojas
            drawLine(a.luz(CERA, k).copy(alpha = 0.75f), Offset(x, 1f * u), Offset(x, h), 0.7f * u)
            drawLine(a.claro(0.24f), Offset(x + 0.7f * u, 1f * u), Offset(x + 0.7f * u, h), 0.4f * u)
        }
        // La hoja de delante: su luz a la izquierda, su sombra a la derecha, la cabeza, el pie y la veta.
        drawRoundRect(boj, Offset.Zero, Size(fr, h), CornerRadius(1.4f * u))
        drawRect(a.claro(0.24f), Offset(0.5f * u, 1f * u), Size(w * 0.09f, h - 1f * u))
        drawRect(a.sombra(0.15f), Offset(fr - w * 0.1f, 0f), Size(w * 0.1f, h))
        drawRect(a.sombra(0.32f), Offset(fr - 0.8f * u, 0f), Size(0.8f * u, h))
        drawRect(a.claro(0.45f), Offset(1f * u, 0f), Size(fr - 2f * u, 0.9f * u))
        for (j in 0 until 3) {
            val x = fr * (0.16f + 0.3f * j + 0.1f * azar(s, 30 + j))
            drawLine(a.sombra(0.1f), Offset(x, h * 0.02f), Offset(x + (azar(s, 40 + j) - 0.5f) * 3f * u, h), 0.6f * u)
        }
        // Una esquina mellada del uso.
        drawPath(poligono(0f, h, 0f, h - (2f + 3f * azar(s, 5)) * u, (2f + 2f * azar(s, 6)) * u, h), a.sombra(0.3f))
        // La ventana de cera, hundida en el marco, con el día y el mes incisos.
        val ven = Rect(w * 0.13f, h * 0.055f, fr - w * 0.08f, h * 0.615f)
        drawRect(a.luz(CERA, k), ven.topLeft, ven.size)
        drawRect(a.sombra(0.5f), ven.topLeft, Size(ven.width, 1.8f * u))
        drawRect(a.sombra(0.3f), ven.topLeft, Size(1.3f * u, ven.height))
        drawRect(a.claro(0.3f), Offset(ven.left, ven.bottom), Size(ven.width, 0.8f * u))
        drawRect(a.claro(0.16f), Offset(ven.right, ven.top), Size(0.7f * u, ven.height))
        val inc = a.inciso(k)
        textoEn(medidor, v.dia, Rect(ven.left, ven.top + 1.5f * u, ven.right, ven.top + 21f * u), LETRA, inc, 15f, peso = FontWeight.SemiBold)
        textoEn(medidor, v.mes, Rect(ven.left + 1f * u, ven.top + 22f * u, ven.right - 1f * u, ven.bottom - 2f * u), LETRA, inc.copy(alpha = 0.85f), 9f, vertical = true, minimo = 4.5f)
        // El cordón, que pasa por los dos agujeros del canto izquierdo y da sus vueltas: es el color del ánimo.
        val cordon = v.tono?.let { a.animo(it, k) } ?: a.luz(mezcla(HUESO, CUERO, 0.28f), k)
        val hx = w * 0.065f
        drawCircle(a.sombra(0.5f), 1f * u, Offset(hx, h * 0.14f))
        drawLine(cordon, Offset(hx, h * 0.14f), Offset(hx, h * 0.67f), 1f * u)
        val yc = h * 0.665f; val gc = 7.2f * u
        drawRect(a.sombra(0.3f), Offset(0f, yc + gc), Size(w, 1.1f * u))
        drawRect(cordon, Offset(0f, yc), Size(w, gc))
        for (j in 1..3) drawLine(mezcla(cordon, a.hondo, 0.4f), Offset(0f, yc + gc * j / 4f + 0.4f * u), Offset(w, yc + gc * j / 4f - 0.4f * u), 0.6f * u)
        drawRect(a.claro(0.3f), Offset(0f, yc), Size(w * 0.55f, 0.7f * u))
        drawRect(a.sombra(0.22f), Offset(fr - w * 0.06f, yc), Size(w - fr + w * 0.06f, gc))
        val nudo = Offset(w * 0.3f, yc + gc * 0.5f)
        drawLine(cordon, nudo, Offset(nudo.x - 1.8f * u, yc + gc + 6.5f * u), 1.3f * u, StrokeCap.Round)
        drawLine(cordon, nudo, Offset(nudo.x + 2.4f * u, yc + gc + 5f * u), 1.3f * u, StrokeCap.Round)
        drawCircle(mezcla(cordon, a.hondo, 0.3f), 2.1f * u, nudo)
        drawCircle(a.claro(0.4f), 0.7f * u, nudo - Offset(0.6f * u, 0.6f * u))
        // Al pie, la marca.
        marca(Offset(fr * 0.56f, h * 0.885f), minOf(fr * 0.24f, h * 0.048f), v.noche, mezcla(boj, a.luz(MADERA_OSC, k), 0.8f), a, k)
        drawRect(a.sombra(0.28f), Offset(0f, h - 1.4f * u), Size(w, 1.4f * u))
        filoVolumen(a, k, 0f, fr)
    }

    /**
     * El políptico: varias tablillas atadas, vistas de canto (más tablillas cuanto más escribiste), más bajo que el pinax y de
     * cabezas desiguales. Lo ciñen dos correas del color del ánimo; entre ellas cuelga la etiqueta de hueso con el día y va el
     * mes inciso, y de la de abajo pende el sello de barro con su hoja de olivo (en los Sueños, la luna).
     */
    private fun DrawScope.poliptico(v: Volumen, medidor: TextMeasurer, a: Aire, k: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val n = 3 + (v.lleno * 3.4f).toInt()
        val madera = a.luz(mezcla(MADERA, BOJ, 0.3f + 0.25f * azar(s, 2)), k)
        val tope = 2.6f * u
        drawRect(a.luz(mezcla(CERA, MADERA_OSC, 0.4f), k), Offset(0.3f * u, tope), Size(w - 0.6f * u, h - tope))
        val paso = w / n
        for (j in 0 until n) {
            val dy = tope * azar(s, 20 + j)
            val col = mezcla(madera, if (j % 2 == 0) a.hondo else a.luz(BOJ, k), 0.1f + 0.08f * azar(s, 60 + j))
            drawRect(col, Offset(j * paso, dy), Size(paso - 0.8f * u, h - dy))
            drawRect(a.claro(0.4f), Offset(j * paso, dy), Size(paso - 0.8f * u, 0.8f * u))
            drawRect(a.claro(0.16f), Offset(j * paso, dy), Size(0.7f * u, h - dy))
        }
        drawRect(a.claro(0.14f), Offset(0.7f * u, tope), Size(w * 0.08f, h - tope))
        drawRect(a.sombra(0.2f), Offset(w * 0.78f, tope), Size(w * 0.22f, h - tope))
        drawRect(a.sombra(0.14f), Offset(w * 0.9f, tope), Size(w * 0.1f, h - tope))
        // Las dos correas: el color del ánimo (de cuero sin teñir si no se marcó).
        val correa = v.tono?.let { a.animo(it, k) } ?: a.luz(CUERO, k)
        val g = 5.6f * u
        val y1 = h * 0.1f; val y2 = h * 0.735f
        for (y in listOf(y1, y2)) {
            drawRect(a.sombra(0.34f), Offset(0f, y + g), Size(w, 1.1f * u))
            drawRect(correa, Offset(0f, y), Size(w, g))
            drawRect(a.claro(0.3f), Offset(0f, y), Size(w * 0.6f, 0.7f * u))
            drawRect(mezcla(correa, a.hondo, 0.35f), Offset(0f, y + g - 1f * u), Size(w, 1f * u))
            drawRect(a.sombra(0.22f), Offset(w * 0.78f, y), Size(w * 0.22f, g))
            // La hebilla de la correa: una pasada doble.
            drawRect(mezcla(correa, a.hondo, 0.3f), Offset(w * 0.16f, y - 0.6f * u), Size(2.2f * u, g + 1.2f * u))
        }
        // La etiqueta de hueso, colgada de la correa de arriba, con el día.
        val et = Rect(w * 0.09f, y1 + g + 2.6f * u, w * 0.91f, y1 + g + 21.6f * u)
        drawLine(a.luz(HUESO, k), Offset(w * 0.5f, y1 + g), Offset(w * 0.5f, et.top + 1f * u), 0.7f * u)
        drawRect(a.sombra(0.4f), et.topLeft + Offset(0.9f * u, 1.1f * u), et.size)
        drawRoundRect(a.etiqueta(k), et.topLeft, et.size, CornerRadius(1.2f * u))
        drawRect(a.sombra(0.12f), Offset(et.right - et.width * 0.16f, et.top), Size(et.width * 0.16f, et.height))
        textoEn(medidor, v.dia, Rect(et.left + 0.6f * u, et.top + 0.6f * u, et.right - 0.6f * u, et.bottom - 0.4f * u), LETRA, a.tinta, 14.5f, peso = FontWeight.SemiBold)
        // El mes, inciso en la madera, de abajo arriba.
        val mes = Rect(w * 0.14f, et.bottom + 2f * u, w * 0.62f, y2 - 1.5f * u)
        if (mes.height > 9f * u) textoEn(medidor, v.mes, mes, LETRA, a.inciso(k).copy(alpha = 0.92f), 8f, vertical = true, minimo = 4.5f)
        // El sello de barro, que pende de la correa de abajo.
        val sello = Offset(w * 0.72f, y2 + g + 5.2f * u)
        val rs = minOf(4.6f * u, w * 0.2f)
        drawLine(correa, Offset(sello.x, y2 + g), sello, 0.9f * u)
        val barro = a.luz(mezcla(TERRACOTA, MADERA, 0.25f), k)
        // Una pella de barro aplastada con el dedo, no un disco: con su reborde y la impronta en el medio.
        drawOval(barro, Offset(sello.x - rs * 1.08f, sello.y - rs * 0.9f), Size(rs * 2.16f, rs * 1.8f))
        drawOval(barro, Offset(sello.x - rs * 0.8f, sello.y - rs), Size(rs * 1.5f, rs * 2f))
        drawArc(a.claro(0.4f), 170f, 110f, false, Offset(sello.x - rs * 0.9f, sello.y - rs * 0.78f), Size(rs * 1.8f, rs * 1.56f), style = Stroke(0.7f * u))
        if (v.noche) {
            drawCircle(if (a.oscuro) mezcla(LUNA, a.hondo, 0.2f - 0.2f * k) else LUNA, rs * 0.66f, sello)
            drawCircle(barro, rs * 0.56f, sello + Offset(rs * 0.3f, -rs * 0.18f))
        } else {
            drawOval(mezcla(barro, a.hondo, 0.34f), Offset(sello.x - rs * 0.56f, sello.y - rs * 0.4f), Size(rs * 1.12f, rs * 0.86f))
            drawOval(mezcla(barro, a.luz(HUESO, k), 0.25f), Offset(sello.x - rs * 0.44f, sello.y - rs * 0.24f), Size(rs * 0.96f, rs * 0.66f))
            drawOval(mezcla(barro, a.hondo, 0.4f), Offset(sello.x - rs * 0.2f, sello.y - rs * 0.1f), Size(rs * 0.5f, rs * 0.28f))
        }
        drawRect(a.sombra(0.28f), Offset(0f, h - 1.4f * u), Size(w, 1.4f * u))
        filoVolumen(a, k, tope, w)
    }

    /**
     * El rollo de papiro, de pie en su funda de pergamino teñida del color del ánimo: arriba asoma la boca del rollo, con las
     * vueltas de la hoja (más cuanto más escribiste) y el umbilicus; del umbilicus cuelga la etiqueta (el sillybos) con el día;
     * en la funda, el mes de abajo arriba y, al pie, el ramito de olivo (en los Sueños, la luna).
     */
    private fun DrawScope.rollo(v: Volumen, medidor: TextMeasurer, a: Aire, k: Float) {
        val u = density
        val w = size.width; val h = size.height
        val r = w / 2
        val rk = minOf(w * 0.1f, 3.2f * u)
        val met = w * 0.08f                      // lo que el rollo es más estrecho que su funda, por lado
        val ell = (w - 2 * met) * 0.3f
        val yBoca = rk * 2f + ell * 0.36f
        val yFunda = yBoca + h * 0.085f
        val esq = r * 0.36f
        val papiro = a.luz(PAPIRO, k)
        // El rollo, que asoma de la funda: las vueltas de la hoja y la orilla donde acaba.
        drawRect(papiro, Offset(met, yBoca), Size(w - 2 * met, yFunda - yBoca + 3f * u))
        drawRect(a.claro(0.3f), Offset(met + w * 0.1f, yBoca), Size(w * 0.13f, yFunda - yBoca + 3f * u))
        drawRect(a.sombra(0.2f), Offset(w * 0.68f, yBoca), Size(w - met - w * 0.68f, yFunda - yBoca + 3f * u))
        drawLine(a.luz(mezcla(PAPIRO, CUERO, 0.55f), k), Offset(w * 0.6f, yBoca + ell * 0.4f), Offset(w * 0.6f, yFunda + 2f * u), 0.6f * u)
        // La funda de pergamino teñido: la boca fruncida, en ondas, y el cuerpo hasta el pie.
        val cuerpo = Path().apply {
            moveTo(0f, yFunda + 2.4f * u)
            for (j in 0 until 4) cubicTo(w * (j + 0.3f) / 4f, yFunda - 1.2f * u, w * (j + 0.7f) / 4f, yFunda - 1.2f * u, w * (j + 1f) / 4f, yFunda + (if (j == 3) 2.4f else 1.4f) * u)
            lineTo(w, h - esq); cubicTo(w, h, w, h, w - esq, h); lineTo(esq, h); cubicTo(0f, h, 0f, h, 0f, h - esq); close()
        }
        val funda = v.tono?.let { a.animo(it, k) } ?: a.luz(mezcla(HUESO, MIMBRE, 0.3f), k)
        drawPath(cuerpo, funda)
        clipPath(cuerpo) {
            // El cilindro, sin degradados: una franja de luz a la izquierda y dos de sombra a la derecha.
            drawRect(a.sombra(0.14f), Offset(0f, yBoca), Size(w * 0.07f, h))
            drawRect(a.claro(0.3f), Offset(w * 0.14f, yBoca), Size(w * 0.15f, h))
            drawRect(a.sombra(0.15f), Offset(w * 0.66f, yBoca), Size(w * 0.34f, h))
            drawRect(a.sombra(0.2f), Offset(w * 0.85f, yBoca), Size(w * 0.15f, h))
            // Los pliegues que bajan del fruncido, el roce del pie y la costura.
            for (j in 0 until 4) drawLine(a.sombra(0.2f), Offset(w * (j + 0.5f) / 4f + 0.6f * u, yFunda + 6.5f * u), Offset(w * (j + 0.5f) / 4f + (j - 1.5f) * 0.5f * u, yFunda + (11f + 3f * azar(v.semilla, 50 + j)) * u), 0.7f * u)
            drawRect(a.claro(0.16f), Offset(0f, h - 4.6f * u), Size(w, 1.2f * u))
            drawRect(a.sombra(0.28f), Offset(0f, h - 1.5f * u), Size(w, 1.5f * u))
            for (j in 0 until 8) drawLine(a.sombra(0.32f), Offset(w * 0.82f, yFunda + (16f + 9f * j) * u), Offset(w * 0.82f, yFunda + (20f + 9f * j) * u), 0.6f * u)
        }
        // El cordón que frunce la boca, con su lazada.
        val lazo = mezcla(funda, a.hondo, 0.5f)
        drawLine(lazo, Offset(0.4f * u, yFunda + 4.6f * u), Offset(w - 0.4f * u, yFunda + 4.6f * u), 1.3f * u)
        drawLine(a.claro(0.25f), Offset(0.4f * u, yFunda + 3.8f * u), Offset(w * 0.6f, yFunda + 3.8f * u), 0.5f * u)
        drawLine(lazo, Offset(w * 0.74f, yFunda + 4.6f * u), Offset(w * 0.68f, yFunda + 9.5f * u), 1.1f * u, StrokeCap.Round)
        drawLine(lazo, Offset(w * 0.74f, yFunda + 4.6f * u), Offset(w * 0.8f, yFunda + 9f * u), 1.1f * u, StrokeCap.Round)
        // La boca del rollo: la elipse con sus vueltas y, en el medio, el umbilicus con su pomo.
        drawOval(a.luz(mezcla(PAPIRO, HUESO, 0.5f), k), Offset(met, yBoca - ell / 2), Size(w - 2 * met, ell))
        drawOval(a.luz(mezcla(PAPIRO, CUERO, 0.6f), k), Offset(met, yBoca - ell / 2), Size(w - 2 * met, ell), style = Stroke(0.6f * u))
        val vueltas = 1 + (v.lleno * 3).toInt()
        for (j in 1..vueltas) {
            val f = 1f - j / (vueltas + 1.3f)
            drawOval(a.luz(mezcla(PAPIRO, CUERO, 0.6f), k), Offset(r - (r - met) * f, yBoca - ell / 2 * f), Size((w - 2 * met) * f, ell * f), style = Stroke(0.6f * u))
        }
        drawRect(a.luz(MADERA_OSC, k), Offset(r - rk * 0.55f, rk * 1.2f), Size(rk * 1.1f, yBoca - rk * 1.2f))
        drawCircle(a.luz(mezcla(MADERA_OSC, BRONCE, 0.45f), k), rk, Offset(r, rk))
        drawCircle(a.claro(0.45f), rk * 0.34f, Offset(r - rk * 0.3f, rk * 0.7f))
        // La etiqueta que cuelga del umbilicus, con el día.
        val et = Rect(w * 0.1f, yFunda + 12f * u, w * 0.9f, yFunda + 31f * u)
        drawLine(a.luz(HUESO, k), Offset(r, yBoca), Offset(r, et.top + 1f * u), 0.7f * u)
        rotate((azar(v.semilla, 8) - 0.5f) * 5f, et.center) {
            drawRect(a.sombra(0.4f), et.topLeft + Offset(0.9f * u, 1.1f * u), et.size)
            drawRoundRect(a.etiqueta(k), et.topLeft, et.size, CornerRadius(1.2f * u))
            drawRect(a.sombra(0.12f), Offset(et.right - et.width * 0.18f, et.top), Size(et.width * 0.18f, et.height))
            textoEn(medidor, v.dia, Rect(et.left + 0.6f * u, et.top + 0.6f * u, et.right - 0.6f * u, et.bottom - 0.4f * u), LETRA, a.tinta, 14.5f, peso = FontWeight.SemiBold)
        }
        // El mes, escrito en la funda, y la marca al pie: con tinta oscura o clara, la que se lea sobre ese color.
        val pluma = if (funda.luminance() > 0.3f) a.tinta.copy(alpha = 0.85f) else a.inciso(k)
        val yMarca = h - 8.5f * u
        val mes = Rect(w * 0.22f, et.bottom + 3f * u, w * 0.72f, yMarca - 6.5f * u)
        if (mes.height > 9f * u) textoEn(medidor, v.mes, mes, LETRA, pluma, 8.5f, vertical = true, minimo = 4.5f)
        marca(Offset(w * 0.47f, yMarca), minOf(w * 0.15f, 3.4f * u), v.noche, pluma, a, k)
        if (a.oscuro && k >= 0.1f) {
            drawArc(FILO_MUEBLE.copy(alpha = 0.6f * k), 180f, 180f, false, Offset(met, yBoca - ell / 2), Size(w - 2 * met, ell), style = Stroke(1f * u))
            clipPath(cuerpo) { drawRect(FILO_MUEBLE.copy(alpha = 0.34f * k), Offset(0f, yBoca), Size(0.9f * u, h)) }
        }
    }
}
