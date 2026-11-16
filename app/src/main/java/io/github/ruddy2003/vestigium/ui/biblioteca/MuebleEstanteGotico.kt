package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * El dibujo del estante del diario de Gótico (lo usa `EstanteGotico`, en `BibliotecaMedievo.kt`): el armario de libros
 * (armarium) de la sala capitular de Amiens, en obra hacia 1240. Un armario-librería de roble empotrado en un nicho del muro
 * de sillares de caliza, bajo un arco de descarga de dovelas; ya sin puertas, le quedan las bisagras de hierro y el pasador
 * con su candado. Todo el dibujo sale de [estante] y de [volumen]; la luz de la noche de los dos sale de la misma curva,
 * [alcance]: la del farol de cuerno colgado en el nicho.
 */
internal object MuebleEstanteGotico {
    // ---------------------------------------------------------------- la paleta (sale del Ideario y del cuadro de Gótico)
    /** La caliza de los sillares, su cara en sombra y el mortero de las juntas. */
    private val CALIZA = Color(0xFFD3C7A9)
    private val CALIZA_OSC = Color(0xFFA39478)
    private val JUNTA = Color(0xFF857A66)
    /** El roble del armario: su luz y lo hondo de la talla. */
    private val ROBLE = Color(0xFF5A3E24)
    private val ROBLE_LUZ = Color(0xFF8A6842)
    private val ROBLE_OSC = Color(0xFF2E1F12)
    /** El hierro forjado de bisagras, cadenas y argollas. */
    private val HIERRO = Color(0xFF34323A)
    private val HIERRO_LUZ = Color(0xFF7A7680)
    /** El latón de broches y cartelas; el oro del cobre dorado de la arqueta y de los bullones. */
    private val LATON = Color(0xFFC29A46)
    private val ORO = Color(0xFFD2A445)
    /** El esmalte azul de Limoges, su turquesa y la grana de los cabujones y del lacre. */
    private val ESMALTE = Color(0xFF2C4A8E)
    private val TURQUESA = Color(0xFF4F8C9A)
    private val GRANA = Color(0xFF9C2A30)
    /** La vitela de etiquetas y hojas, y la tinta. */
    private val VITELA = Color(0xFFEDE3C9)
    private val TINTA = Color(0xFF1E140C)
    /** Los cueros: el blanco de cerdo curtido al alumbre y el pardo de correas y saquitos. */
    private val CUERO_BLANCO = Color(0xFFD9CBA8)
    private val CUERO = Color(0xFF7A5232)
    /** El cuerno del farol y del tintero, el sebo de las velas y el vidrio del reloj. */
    private val CUERNO = Color(0xFFD9AE62)
    private val SEBO = Color(0xFFE6D9AE)
    /** Los terciopelos de los libros de horas (los de la tapa): carmesí, azul, verde, morado y negro. */
    private val TERCIOPELOS = listOf(Color(0xFF7A1424), Color(0xFF1E2C5E), Color(0xFF1F4A34), Color(0xFF4A2152), Color(0xFF2A2226))
    /** Lo hondo del pelo del terciopelo: el ánimo se tiñe hacia aquí para que sea terciopelo y no pastel. */
    private val PELO = Color(0xFF1C1220)
    /** Los registros de seda. */
    private val REGISTROS = listOf(Color(0xFFC9A24E), Color(0xFF9C2A30), Color(0xFF2E7A4C), Color(0xFF34518E))
    /** Los vidrios de colores del farol de los Sueños. */
    private val VIDRIOS = listOf(Color(0xFF3A5CB0), Color(0xFFB02A34), Color(0xFFE0B040), Color(0xFF2E8A54))

    // Las luces: la noche del claustro, la de los Sueños (más azul), el anochecer de los Sueños en claro, la llama y la luna.
    private val NOCHE = Color(0xFF0D0F1B)
    private val NOCHE_SUENOS = Color(0xFF0E1430)
    private val ANOCHECER = Color(0xFF2B315C)
    private val LLAMA = Color(0xFFFFC56E)
    private val FUEGO = Color(0xFFFFE2A8)
    private val LUNA = Color(0xFFE6E8F0)

    private val LETRA: FontFamily = LetraExtra.goudy

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.56f
    const val BASE = 0.22f
    const val LADO = 0.065f
    val ALTO_REPISA: Dp = 114.dp
    val TABLA: Dp = 18.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (24 + (palabras / 40).coerceAtMost(18)).dp

    /** Cuál de los tres es una entrada: 0, el libro de horas de terciopelo; 1, el códice de tablas forradas; 2, el atado de cuadernillos. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod(semilla xor (semilla ushr 7), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** El libro de horas, mediano; el códice, el más alto; el atado de cuadernillos, el más bajo. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.74f + 0.1f * azar(semilla, 1)
        1 -> 0.9f + 0.08f * azar(semilla, 1)
        else -> 0.58f + 0.08f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde la vela del farol, en anchos del mueble desde la esquina de arriba a la izquierda. */
    private const val LAMP_X = 0.165f
    private const val LAMP_Y = 0.29f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    private const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    private const val FILA = 132f
    /** En la base: dónde empieza el suelo, en fracción de su alto. */
    private const val SUELO = 0.5f
    /** Dónde arranca el arco del nicho (los costados del vano) y su radio, en anchos. */
    private const val JAMBA = 0.03f
    private const val RADIO_ARCO = 0.5f

    /**
     * La curva de la luz del farol, la misma para el mueble y para los volúmenes: cuánta llega (de 1 a 0) a un punto que está
     * a [dx] y [dy] de la llama, en anchos del mueble. Se abre más a lo ancho que hacia abajo (el farol cuelga delante del
     * nicho y alumbra la repisa de arriba de punta a punta); en un estante corto se acaba antes, para que la última repisa
     * quede siempre en penumbra.
     */
    fun alcance(dx: Float, dy: Float, repisas: Int): Float {
        val radio = 0.92f + 0.12f * repisas.coerceIn(2, 5)
        val x = dx * 0.62f
        return (1f - sqrt(x * x + dy * dy) / radio).coerceIn(0f, 1f)
    }

    /** La luz que llega al cuerpo del mueble: [pos] cuenta repisas desde la de arriba (1.5 es media altura de la segunda). */
    fun cercaCuerpo(pos: Float, repisas: Int, fx: Float): Float =
        alcance(fx - LAMP_X, CORONA - LAMP_Y + pos * FILA / ANCHO_NOMINAL, repisas)

    /**
     * El aire del momento: de día, de noche (el modo oscuro) y en los Sueños ([noche]: al anochecer en claro, la noche más
     * azul en oscuro). De él salen el color de cada cosa según cuánta luz del farol le llega, la sombra, la luz y el filo.
     */
    private class Aire(val oscuro: Boolean, val noche: Boolean) {
        /** Si arde el farol. */
        val lumbre: Boolean get() = oscuro || noche
        /** El color en que se hunde lo que no se alumbra. */
        val hondo: Color get() = if (oscuro) (if (noche) NOCHE_SUENOS else NOCHE) else SOMBRA_MUEBLE

        fun luz(c: Color, cerca: Float): Color {
            val k = cerca.coerceIn(0f, 1f)
            if (!oscuro) return if (noche) mezcla(mezcla(c, ANOCHECER, 0.42f - 0.22f * k), LLAMA, 0.12f * k) else c
            return mezcla(mezcla(c, hondo, 0.9f - 0.92f * k.pow(0.85f)), LLAMA, 0.32f * k)
        }
        fun sombra(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)
        fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.12f else if (noche) a * 0.5f else a)
        /** El filo del farol en lo que lo mira; de día no hay. */
        fun filo(a: Float): Color = LLAMA.copy(alpha = if (oscuro) a.coerceIn(0f, 1f) else if (noche) (a * 0.4f).coerceIn(0f, 1f) else 0f)
        /** El color del ánimo: de noche se apaga menos que lo demás, para que se siga viendo en la repisa más lejana. */
        fun animo(t: Color, cerca: Float): Color {
            val k = cerca.coerceIn(0f, 1f)
            if (!oscuro) return if (noche) mezcla(t, ANOCHECER, 0.2f) else t
            return mezcla(mezcla(t, hondo, 0.8f - 0.76f * k.pow(0.8f)), LLAMA, 0.14f * k)

        }
        /** La vitela donde va el día: casi no se apaga, para que el número se lea siempre. */
        fun etiqueta(cerca: Float): Color {
            val k = cerca.coerceIn(0f, 1f)
            if (!oscuro) return if (noche) mezcla(VITELA, ANOCHECER, 0.12f) else VITELA
            return mezcla(mezcla(VITELA, hondo, 0.42f - 0.36f * k), LLAMA, 0.16f * k)
        }
        val tinta: Color get() = TINTA
    }

    // ---------------------------------------------------------------- ayudantes

    private fun corrido(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

    /** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
    private fun borde(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corrido(p, dx, dy), PathOperation.Difference) }

    private fun poligono(vararg xy: Float) = Path().apply {
        moveTo(xy[0], xy[1])
        for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
        close()
    }

    private fun ojiva(r: Rect, flecha: Float) = Path().apply {
        val cx = r.center.x
        moveTo(r.left, r.bottom); lineTo(r.left, r.top + flecha)
        cubicTo(r.left, r.top + flecha * 0.45f, cx - r.width * 0.22f, r.top + flecha * 0.1f, cx, r.top)
        cubicTo(cx + r.width * 0.22f, r.top + flecha * 0.1f, r.right, r.top + flecha * 0.45f, r.right, r.top + flecha)
        lineTo(r.right, r.bottom); close()
    }

    /** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Aire, fuerza: Float = 0.34f) =
        drawOval(a.sombra(if (a.oscuro) fuerza * 1.3f else fuerza), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    /** Un clavo o un bullón visto de frente: su sombra, la cabeza y el punto de luz. */
    private fun DrawScope.clavo(p: Offset, r: Float, color: Color, a: Aire, filo: Float = 0f) {
        drawCircle(a.sombra(0.4f), r * 1.05f, p + Offset(r * 0.35f, r * 0.45f))
        drawCircle(color, r, p)
        drawCircle(a.claro(0.55f), r * 0.38f, p - Offset(r * 0.32f, r * 0.32f))
        if (a.oscuro && filo > 0.05f) drawArc(a.filo(0.7f * filo), 160f, 110f, false, Offset(p.x - r, p.y - r), Size(2 * r, 2 * r), style = Stroke(r * 0.4f))
    }

    /** Un punto del arco del nicho: [lado] -1 a la izquierda, 1 a la derecha; [t] de 0 (arranque) a 1 (clave); [extra], más radio. */
    private fun puntoArco(w: Float, sp: Float, lado: Float, t: Float, extra: Float): Offset {
        val r = w * RADIO_ARCO
        val c = Offset(if (lado < 0) w * (JAMBA + RADIO_ARCO) else w * (1f - JAMBA - RADIO_ARCO), sp)
        val fin = acos(((w / 2 - c.x) / r).coerceIn(-1f, 1f))   // el ángulo de la clave
        val ini = if (lado < 0) Math.PI.toFloat() else 0f
        val ang = ini + (fin - ini) * t
        val rr = r + extra
        return Offset(c.x + rr * cos(ang), c.y - rr * sin(ang))
    }

    /** El hueco del arco, de un arranque al otro y cerrado por abajo en [abajo]. */
    private fun arco(w: Float, sp: Float, extra: Float, abajo: Float) = Path().apply {
        val izq = (0..20).map { puntoArco(w, sp, -1f, it / 20f, extra) }
        val der = (0..20).map { puntoArco(w, sp, 1f, it / 20f, extra) }.reversed()
        moveTo(izq[0].x, abajo); izq.forEach { lineTo(it.x, it.y) }; der.forEach { lineTo(it.x, it.y) }; lineTo(der.last().x, abajo); close()
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = Aire(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val b = d.base
        val suelo = b.top + b.height * SUELO
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * u
        val llama = Offset(w * LAMP_X, w * LAMP_Y)
        // La luz en un punto del lienzo: arriba, por la distancia a la llama; en el cuerpo, la misma cuenta que hacen los volúmenes.
        fun cerca(p: Offset): Float =
            if (p.y <= ch) alcance((p.x - llama.x) / w, (p.y - llama.y) / w, n) else cercaCuerpo((p.y - ch) / maxOf(1f, fila), n, p.x / w)
        /** Hacia dónde cae la sombra de algo en [p] sobre la pared: de día, a la derecha y abajo; de noche, lejos de la llama. */
        fun caida(p: Offset, largo: Float): Offset {
            if (!oscuro) return Offset(largo * 1.15f, largo * 0.8f)
            val v = p - llama; val dd = v.getDistance().coerceAtLeast(1f)
            return v / dd * (largo * 2.6f)
        }
        /** La sombra en la pared de lo que está de pie sobre la cornisa: no baja de donde pisa. */
        fun dePie(p: Offset, largo: Float): Offset = caida(p, largo).let { Offset(it.x, minOf(it.y, largo * 0.4f)) }
        val gT = w * 0.046f            // el canto de la cornisa
        val fT = w * 0.016f            // lo que se ve de su cara de arriba
        val apoyo = ch - gT - fT * 0.5f
        val l = d.izquierda.right
        val sp = ch - w * 0.02f        // donde arranca el arco del nicho

        muro(a, ch, sp, llama) { p -> cerca(p) }
        // El halo del farol en la piedra: unas pocas manchas, no un velo.
        if (a.lumbre) {
            val f = if (oscuro) 1f else 0.5f
            mancha(llama, w * 1.05f, LLAMA.copy(alpha = 0.16f * f))
            mancha(llama, w * 0.5f, LLAMA.copy(alpha = 0.26f * f))
            mancha(llama, w * 0.2f, FUEGO.copy(alpha = 0.34f * f))
            if (noche) {
                // Los vidrios de colores del farol de los Sueños dejan sus manchas en la piedra.
                listOf(Offset(-0.1f, -0.1f), Offset(0.13f, -0.05f), Offset(-0.12f, 0.12f), Offset(0.15f, 0.14f)).forEachIndexed { i, o ->
                    val c = llama + Offset(o.x * w, o.y * w)
                    mancha(c, w * 0.075f, VIDRIOS[i].copy(alpha = if (oscuro) 0.3f else 0.2f))

                }
            }
        }

        // ---- el suelo de losas, en perspectiva
        losas(a, suelo, w, h)

        // ---- el fondo de cada repisa: tablas de roble con lancetas ciegas en bajorrelieve
        d.repisas.forEachIndexed { k, r ->
            fondoRepisa(k, r, d.ocupado.getOrNull(k), a) { p -> cerca(p) }
        }

        // ---- las tablas, con su friso de cuadrifolios, y los rótulos de los meses
        d.tablas.forEachIndexed { k, r -> tabla(r, 860 + 7 * k, a) { x -> cerca(Offset(x, r.center.y)) } }
        d.placas.forEach { p -> filacteria(p, medidor, a, cerca(p.r.center)) }

        // ---- los costados: contrafuertes, bisagras y el pasador
        costado(d.izquierda, false, d, a, n, ch, fila)
        costado(d.derecha, true, d, a, n, ch, fila)

        // ---- lo que hay en los huecos libres de las repisas
        d.repisas.forEachIndexed { k, r ->
            val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
            huecoLibre(k, r, oc, a, cerca(Offset((oc + r.right) / 2, r.center.y)))
        }

        // ---- la base: el zócalo con su arquería ciega
        zocalo(Rect(0f, b.top, w, suelo), a) { p -> cerca(p) }

        // ---- la cornisa, que vuela un poco, y los pináculos
        cornisa(Rect(-2f * u, ch - gT, w + 2f * u, ch), fT, a) { x -> cerca(Offset(x, ch - gT)) }
        if (a.lumbre) clipRect(0f, ch - gT - fT, w, ch) { mancha(Offset(llama.x, ch - gT - fT), w * 0.4f, LLAMA.copy(alpha = if (oscuro) 0.4f else 0.18f)) }
        drawRect(a.sombra(if (oscuro) 0.5f else 0.34f), Offset(l, ch), Size(w - 2 * l, 4f * u))
        drawRect(a.sombra(0.16f), Offset(l, ch + 4f * u), Size(w - 2 * l, 6f * u))

        // ---- lo de arriba, de atrás adelante
        velasColgadas(Offset(w * 0.41f, w * 0.115f), w, a, cerca(Offset(w * 0.41f, w * 0.2f)), caida(Offset(w * 0.41f, w * 0.2f), w * 0.012f))
        pinaculo(Offset(l * 0.5f, apoyo), l * 0.72f, w * 0.2f, a, cerca(Offset(l * 0.5f, apoyo - w * 0.1f)), dePie(Offset(l * 0.5f, apoyo), w * 0.012f))
        val pinDer = Offset(w - l * 0.5f, apoyo)
        pinaculo(pinDer, l * 0.72f, w * 0.2f, a, cerca(pinDer - Offset(0f, w * 0.1f)), dePie(pinDer, w * 0.012f))
        utilesCantero(Offset(w * 0.875f, apoyo), w, a, cerca(Offset(w * 0.9f, apoyo - w * 0.12f)), dePie(Offset(w * 0.9f, apoyo), w * 0.012f))
        val pieArq = Offset(w * 0.668f, apoyo - w * 0.004f)
        arqueta(pieArq, w * 0.29f, a, cerca(pieArq - Offset(w * 0.06f, w * 0.1f)), dePie(pieArq, w * 0.014f), noche)
        // El reloj, delante de la esquina de la arqueta (más cerca de quien mira: pisa un poco más abajo).
        val pieReloj = Offset(w * 0.53f, apoyo + w * 0.004f)
        relojArena(pieReloj, w * 0.118f, a, cerca(pieReloj - Offset(0f, w * 0.06f)), Offset.Zero)
        val pieTintero = Offset(w * 0.4f, apoyo)
        tintero(pieTintero, w * 0.05f, a, cerca(pieTintero - Offset(0f, w * 0.04f)), dePie(pieTintero, w * 0.01f))
        rolloSellado(Offset(w * 0.095f, apoyo), w * 0.2f, ch, a, cerca(Offset(w * 0.19f, apoyo)), dePie(Offset(w * 0.19f, apoyo), w * 0.008f))
        farol(a, noche)

        // ---- al pie: el banco de coro, cortado por el borde, y el cesto de los rollos
        banco(Offset(w * 0.0f, h - (h - suelo) * 0.18f), w * 0.26f, (h - suelo) * 0.9f, a, maxOf(cerca(Offset(w * 0.1f, suelo)), 0.12f))
        cesto(Offset(w * 0.86f, h - (h - suelo) * 0.16f), w * 0.17f, (h - suelo) * 0.62f, a, maxOf(cerca(Offset(w * 0.86f, suelo)), 0.12f))

        // ---- la llama, encima de todo, con su propio color
        if (a.lumbre) llamaFarol(a, noche)
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    // ---------------------------------------------------------------- el muro y el nicho

    /** Los sillares, el nicho bajo su arco de dovelas, la luz de la arquería, las marcas de cantero, la humedad y el hollín. */
    private fun DrawScope.muro(a: Aire, ch: Float, sp: Float, llama: Offset, cerca: (Offset) -> Float) {
        val u = density; val w = size.width
        drawRect(a.luz(JUNTA, 0.2f), Offset.Zero, Size(w, ch))
        // Los sillares, en hiladas de alto desigual.
        var y = -w * 0.03f; var fila = 0
        while (y < ch) {
            val alto = w * (0.072f + 0.018f * azar(611, fila))
            var x = -w * 0.15f * azar(612, fila)
            var j = 0
            while (x < w) {
                val largo = w * (0.17f + 0.15f * azar(613 + fila, j))
                val r = Rect(x + 0.9f * u, y + 0.9f * u, x + largo - 0.9f * u, minOf(y + alto, ch) - 0.9f * u)
                if (r.height > 1f) sillar(r, fila * 41 + j, a, 0.55f * cerca(r.center))
                x += largo; j++
            }
            y += alto; fila++
        }
        pinceladas(Rect(0f, 0f, w, ch), a.luz(CALIZA_OSC, 0.2f), 641, 40, u, if (a.oscuro) 0.05f else 0.09f)

        // El nicho: el fondo hundido, en sombra, con la sombra del arco y de la jamba izquierda arriba a la izquierda.
        val hueco = arco(w, sp, 0f, ch)
        drawPath(hueco, a.sombra(if (a.oscuro) 0.3f else 0.2f))
        clipPath(hueco) {
            drawPath(borde(hueco, w * 0.07f, w * 0.05f), a.sombra(if (a.oscuro) 0.22f else 0.2f))
            drawPath(borde(hueco, w * 0.03f, w * 0.02f), a.sombra(0.16f))
            pinceladas(Rect(w * 0.5f, 0f, w, ch), a.sombra(1f), 642, 18, u, 0.06f)
            // La humedad que baja de una junta y el hollín del farol sobre él.
            mancha(Offset(w * LAMP_X + w * 0.01f, llama.y - w * 0.15f), w * 0.11f, a.sombra(0.45f))
            for (k in 0 until 4) {
                val x0 = w * (0.58f + 0.05f * k + 0.02f * azar(643, k))
                drawLine(a.luz(mezcla(CALIZA_OSC, JUNTA, 0.6f), 0.2f).copy(alpha = 0.3f), Offset(x0, w * 0.05f), Offset(x0 + w * 0.006f, w * (0.18f + 0.1f * azar(644, k))), (2f + 3f * azar(645, k)) * u, StrokeCap.Round)
            }
        }
        // El arco de descarga: dovelas de caliza alrededor del hueco, con la clave arriba.
        val grueso = w * 0.052f
        val tramos = 8
        for (lado in listOf(-1f, 1f)) for (i in 0 until tramos) {
            val t0 = i / tramos.toFloat(); val t1 = (i + 1) / tramos.toFloat()
            val p0 = puntoArco(w, sp, lado, t0, 0f); val p1 = puntoArco(w, sp, lado, t1, 0f)
            val q0 = puntoArco(w, sp, lado, t0, grueso); val q1 = puntoArco(w, sp, lado, t1, grueso)
            val dov = poligono(p0.x, p0.y, q0.x, q0.y, q1.x, q1.y, p1.x, p1.y)
            val semilla = 650 + i + (if (lado > 0) 20 else 0)
            val c = (p0 + q1) / 2f
            drawPath(dov, a.luz(mezcla(CALIZA, CALIZA_OSC, 0.1f + 0.35f * azar(semilla, 1)), 0.55f * cerca(c)))
            drawPath(dov, a.sombra(0.32f), style = Stroke(1.3f * u))
            // La cara de abajo de cada dovela toma la luz si mira a la izquierda (el lado derecho del arco).
            drawLine(if (lado > 0) a.claro(0.4f) else a.sombra(0.3f), p0, p1, 1.4f * u)
            drawLine(a.claro(0.24f), q0 + Offset(0f, 1f * u), q1 + Offset(0f, 1f * u), 0.8f * u)
        }
        // La clave.
        val k0 = puntoArco(w, sp, -1f, 1f, 0f)
        val clave = poligono(k0.x - w * 0.026f, k0.y + w * 0.004f, k0.x - w * 0.034f, k0.y - grueso - w * 0.008f, k0.x + w * 0.034f, k0.y - grueso - w * 0.008f, k0.x + w * 0.026f, k0.y + w * 0.004f)
        drawPath(corrido(clave, 2f * u, 2f * u), a.sombra(0.35f))
        drawPath(clave, a.luz(mezcla(CALIZA, LUZ_MUEBLE, 0.15f), 0.55f * cerca(k0)))
        drawPath(borde(clave, -1.4f * u, -1.4f * u), a.sombra(0.25f))
        // La sombra que el arco deja en el fondo del nicho, abajo y a la derecha del intradós.
        clipPath(hueco) { drawPath(borde(arco(w, sp, 0f, ch), -w * 0.018f, -w * 0.024f), a.sombra(0.12f)) }

        // La luz de día, de una arquería que no se ve: una mancha oblicua partida por la sombra de una columna.
        if (!a.oscuro) {
            val f = if (a.noche) 0.35f else 1f
            val izq = poligono(-w * 0.02f, w * 0.02f, w * 0.12f, -w * 0.01f, w * 0.44f, ch * 0.92f, w * 0.26f, ch * 0.98f)
            val der = poligono(w * 0.19f, -w * 0.01f, w * 0.36f, -w * 0.01f, w * 0.72f, ch * 0.86f, w * 0.52f, ch * 0.92f)
            for (p in listOf(izq, der)) {
                drawPath(p, luzMueble(0.16f * f))
                drawPath(borde(p, w * 0.012f, 0f).let { q -> Path().apply { op(p, q, PathOperation.Difference) } }, luzMueble(0.16f * f))
            }
        }

        // Las marcas de cantero, distintas de las de la sacristía: el cuatro, la rueda partida y el zigzag.
        listOf(Triple(0.08f, 0.09f, 0), Triple(0.9f, 0.08f, 1), Triple(0.52f, 0.2f, 2), Triple(0.28f, 0.44f, 1), Triple(0.84f, 0.4f, 0)).forEach { (fx, fy, tipo) ->
            val c = Offset(w * fx, w * fy)
            marca(c, w * 0.022f, tipo, a, cerca(c))
        }
        // La argolla de hierro, empotrada en la piedra.
        argolla(Offset(w * 0.6f, w * 0.13f), w * 0.022f, a, cerca(Offset(w * 0.6f, w * 0.13f)))
    }

    /** Un sillar de caliza: su tono, la labra a trinchante, el canto de arriba a la luz, el de abajo en sombra y algún desconchón. */
    private fun DrawScope.sillar(r: Rect, s: Int, a: Aire, k: Float) {
        val u = density
        var tono = mezcla(CALIZA, CALIZA_OSC, 0.05f + 0.36f * azar(s, 1))
        if (azar(s, 2) > 0.74f) tono = mezcla(tono, Color(0xFFC9B088), 0.32f)
        drawRect(a.luz(tono, k), r.topLeft, r.size)
        val ow = r.width * (0.3f + 0.4f * azar(s, 10)); val oh = r.height * (0.4f + 0.3f * azar(s, 20))
        drawOval(a.luz(mezcla(tono, CALIZA_OSC, 0.4f), k).copy(alpha = 0.4f), Offset(r.left + (r.width - ow) * azar(s, 30), r.top + (r.height - oh) * azar(s, 40)), Size(ow, oh))
        // La labra: rayas finas oblicuas del trinchante.
        for (j in 0 until 3) {
            val x = r.left + r.width * (0.15f + 0.3f * j + 0.1f * azar(s, 50 + j))
            drawLine(a.sombra(0.1f), Offset(x, r.top + 2f * u), Offset(x + r.height * 0.3f, r.bottom - 2f * u), 0.6f * u)
        }
        drawRect(a.claro(0.3f), r.topLeft, Size(r.width, 1.1f * u))
        drawRect(a.claro(0.16f), r.topLeft, Size(1f * u, r.height))
        drawRect(a.sombra(0.26f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
        drawRect(a.sombra(0.16f), Offset(r.right - 1f * u, r.top), Size(1f * u, r.height))
        if (azar(s, 3) > 0.72f) {
            val e = r.height * (0.2f + 0.2f * azar(s, 4))
            val x = if (azar(s, 5) > 0.5f) r.right else r.left
            val sx = if (x == r.right) -1f else 1f
            drawPath(poligono(x, r.top, x + sx * e * 1.3f, r.top, x + sx * e * 0.4f, r.top + e * 0.6f, x, r.top + e), a.sombra(0.3f))
            drawLine(a.claro(0.35f), Offset(x + sx * e * 0.4f, r.top + e * 0.6f), Offset(x, r.top + e), 0.8f * u)
        }
    }

    /** Una marca de cantero grabada: el cuatro (0), la rueda partida (1) o el zigzag (2). */
    private fun DrawScope.marca(c: Offset, s: Float, tipo: Int, a: Aire, k: Float) {
        val u = density
        val p = Path()
        when (tipo) {
            0 -> { p.moveTo(c.x, c.y + s * 0.5f); p.lineTo(c.x, c.y - s * 0.5f); p.lineTo(c.x - s * 0.36f, c.y); p.lineTo(c.x + s * 0.3f, c.y); p.moveTo(c.x, c.y - s * 0.5f); p.lineTo(c.x + s * 0.3f, c.y - s * 0.32f) }
            1 -> { p.addOval(Rect(c.x - s * 0.4f, c.y - s * 0.4f, c.x + s * 0.4f, c.y + s * 0.4f)); p.moveTo(c.x - s * 0.4f, c.y + s * 0.3f); p.lineTo(c.x + s * 0.4f, c.y - s * 0.3f) }
            else -> { p.moveTo(c.x - s * 0.5f, c.y - s * 0.2f); p.lineTo(c.x - s * 0.25f, c.y + s * 0.3f); p.lineTo(c.x, c.y - s * 0.2f); p.lineTo(c.x + s * 0.25f, c.y + s * 0.3f); p.lineTo(c.x + s * 0.5f, c.y - s * 0.2f) }
        }
        drawPath(corrido(p, 0.7f * u, 0.7f * u), a.claro(0.4f).let { if (a.oscuro) a.filo(0.2f * k) else it }, style = Stroke(0.7f * u, cap = StrokeCap.Round))
        drawPath(p, a.sombra(0.5f), style = Stroke(0.95f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    /** Una argolla de hierro: el cáncamo empotrado y la anilla colgando, con su sombra en la piedra. */
    private fun DrawScope.argolla(c: Offset, r: Float, a: Aire, k: Float) {
        val u = density
        drawOval(a.sombra(0.3f), Offset(c.x - r * 0.5f + r * 0.3f, c.y + r * 0.3f), Size(r * 1.4f, r * 2.2f), style = Stroke(r * 0.22f))
        drawCircle(a.sombra(0.4f), r * 0.32f, c + Offset(r * 0.12f, r * 0.12f))
        drawCircle(a.luz(HIERRO, k), r * 0.28f, c)
        drawOval(a.luz(HIERRO, k), Offset(c.x - r * 0.62f, c.y + r * 0.05f), Size(r * 1.24f, r * 2f), style = Stroke(r * 0.2f))
        drawArc(a.luz(HIERRO_LUZ, k), 150f, 80f, false, Offset(c.x - r * 0.62f, c.y + r * 0.05f), Size(r * 1.24f, r * 2f), style = Stroke(r * 0.08f))
        if (a.oscuro) drawArc(a.filo(0.6f * k), 140f, 90f, false, Offset(c.x - r * 0.62f, c.y + r * 0.05f), Size(r * 1.24f, r * 2f), style = Stroke(0.8f * u))
    }

    /** Las losas del suelo en tres hileras, más grandes cuanto más cerca, con sus juntas que huyen hacia el fondo y el fregado. */
    private fun DrawScope.losas(a: Aire, suelo: Float, w: Float, h: Float) {
        val u = density
        val alto = h - suelo
        if (alto < 1f) return
        drawRect(a.luz(JUNTA, 0.1f), Offset(0f, suelo), Size(w, alto))
        val cortes = listOf(0f, 0.24f, 0.56f, 1f)
        for (f in 0 until 3) {
            val y0 = suelo + alto * cortes[f]; val y1 = suelo + alto * cortes[f + 1]
            val paso = w * (0.16f + 0.08f * f)
            var x = -paso * azar(671, f)
            var j = 0
            while (x < w) {
                val l0 = x + 0.9f * u; val l1 = x + paso - 0.9f * u
                // Las juntas huyen hacia un punto alto en el medio.
                fun fx(xx: Float, yy: Float) = w / 2 + (xx - w / 2) * (1f + 0.5f * (yy - suelo) / alto)
                val p = poligono(fx(l0, y0 + 0.8f * u), y0 + 0.8f * u, fx(l1, y0 + 0.8f * u), y0 + 0.8f * u, fx(l1, y1 - 0.8f * u), y1 - 0.8f * u, fx(l0, y1 - 0.8f * u), y1 - 0.8f * u)
                val tono = mezcla(mezcla(CALIZA_OSC, JUNTA, 0.3f), CALIZA, 0.1f + 0.3f * azar(672 + f, j))
                drawPath(p, a.luz(tono, 0.12f))
                drawLine(a.claro(0.25f), Offset(fx(l0, y0 + 0.8f * u), y0 + 0.8f * u), Offset(fx(l1, y0 + 0.8f * u), y0 + 0.8f * u), 0.8f * u)
                x += paso; j++
            }
        }
        pinceladas(Rect(0f, suelo, w, h), a.luz(mezcla(CALIZA, LUZ_MUEBLE, 0.4f), 0.1f), 673, 20, u, if (a.oscuro) 0.04f else 0.1f)
        // La sombra del armario en el suelo, corrida a la derecha.
        drawPath(poligono(0f, suelo, w, suelo, w, suelo + alto * 0.36f, w * 0.06f, suelo + alto * 0.36f), a.sombra(if (a.oscuro) 0.45f else 0.24f))
        drawRect(a.sombra(0.3f), Offset(0f, suelo), Size(w, 2f * u))
    }

    // ---------------------------------------------------------------- el armario

    /**
     * El fondo de una repisa: tablas de roble verticales con lancetas ciegas talladas en bajorrelieve (que se leen por la luz:
     * el borde de arriba a la izquierda en sombra y el de abajo a la derecha con luz), la sombra de la tabla de arriba, el
     * costado derecho más hondo y la sombra del último volumen.
     */
    private fun DrawScope.fondoRepisa(k: Int, r: Rect, oc: Float?, a: Aire, cerca: (Offset) -> Float) {
        val u = density
        clipRect(r.left, r.top, r.right, r.bottom) {
            val tramos = if (a.lumbre) 24 else 1
            for (t in 0 until tramos) {
                val xa = r.left + r.width * t / tramos; val xb = r.left + r.width * (t + 1) / tramos + 0.6f
                drawRect(a.luz(mezcla(ROBLE, ROBLE_OSC, 0.45f), cerca(Offset((xa + xb) / 2, r.center.y))), Offset(xa, r.top), Size(xb - xa, r.height))
            }
            vetas(r, ROBLE_OSC.copy(alpha = if (a.oscuro) 0.4f else 0.5f), 801 + k, densidad = 0.5f, vertical = true, grosor = 0.7f)
            // Las lancetas ciegas.
            val paso = 27f * u
            val an = 19f * u
            var x = r.left + 5f * u
            var i = 0
            while (x + an < r.right - 2f * u) {
                val lr = Rect(x, r.top + 16f * u, x + an, r.bottom - 3f * u)
                val kk = cerca(lr.center)
                val p = ojiva(lr, an * 0.95f)
                drawPath(p, a.luz(mezcla(ROBLE, ROBLE_OSC, 0.62f), kk))
                drawPath(borde(p, 2.4f * u, 2.6f * u), a.sombra(if (a.oscuro) 0.35f else 0.42f))
                drawPath(borde(p, -1.4f * u, -1.4f * u), if (a.oscuro) a.filo(0.35f * kk) else a.claro(0.2f))
                // El trébol calado arriba.
                val tc = Offset(lr.center.x, lr.top + an * 0.78f)
                drawCircle(a.sombra(0.4f), an * 0.2f, tc + Offset(0.8f * u, 0.8f * u))
                drawCircle(a.luz(ROBLE, kk), an * 0.2f, tc, style = Stroke(1.1f * u))
                // La junta entre dos tablas del fondo.
                drawLine(a.sombra(0.4f), Offset(x + an + (paso - an) / 2, r.top), Offset(x + an + (paso - an) / 2, r.bottom), 0.8f * u)
                x += paso; i++
            }
            // La sombra de la tabla de arriba: honda junto a ella y deshecha hacia abajo.
            val f = if (a.oscuro) 1.2f else 1f
            drawRect(a.sombra(0.4f * f), r.topLeft, Size(r.width, 8f * u))
            drawRect(a.sombra(0.22f * f), r.topLeft, Size(r.width, 18f * u))
            drawRect(a.sombra(0.1f * f), r.topLeft, Size(r.width, 32f * u))
            // La sombra del costado izquierdo y la luz que entra a su lado.
            drawRect(a.sombra(0.3f), r.topLeft, Size(7f * u, r.height))
            if (!a.oscuro) pinceladas(Rect(r.left + 7f * u, r.top + r.height * 0.3f, r.left + r.width * 0.5f, r.bottom), ROBLE_LUZ, 821 + k, 12, u, if (a.noche) 0.06f else 0.12f)
            // El costado derecho, más hondo.
            pinceladas(Rect(r.right - r.width * 0.3f, r.top, r.right, r.bottom), a.sombra(1f), 825 + k, 18, u, 0.08f)
            drawRect(a.sombra(0.14f), Offset(r.right - 12f * u, r.top), Size(12f * u, r.height))
            drawRect(a.sombra(0.22f), Offset(r.right - 5f * u, r.top), Size(5f * u, r.height))
            // La sombra que arroja el último volumen sobre el fondo, a su derecha.
            if (oc != null && oc > r.left + 6f * u) {
                val largo = (if (a.oscuro) 20f else 13f) * u
                drawPath(poligono(oc - 2f * u, r.bottom, oc - 2f * u, r.top + r.height * 0.22f, oc + largo, r.top + r.height * 0.32f, oc + largo, r.bottom), a.sombra(if (a.oscuro) 0.42f else 0.3f))
                drawPath(poligono(oc + largo, r.bottom, oc + largo, r.top + r.height * 0.32f, oc + largo * 1.5f, r.top + r.height * 0.38f, oc + largo * 1.5f, r.bottom), a.sombra(0.14f))
            }
        }
    }

    /** Una tabla gruesa: su cara de arriba, vista apenas, y el canto con el friso de cuadrifolios en relieve. */
    private fun DrawScope.tabla(r: Rect, semilla: Int, a: Aire, cerca: (Float) -> Float) {
        val u = density
        val fondo = 3.5f * u
        val tramos = if (a.lumbre) 28 else 1
        for (t in 0 until tramos) {
            val xa = r.left + r.width * t / tramos; val xb = r.left + r.width * (t + 1) / tramos + 0.6f
            val k = cerca((xa + xb) / 2)
            drawRect(a.luz(mezcla(ROBLE, ROBLE_LUZ, 0.6f), k), Offset(xa, r.top - fondo), Size(xb - xa, fondo))
            drawRect(a.luz(ROBLE, k), Offset(xa, r.top), Size(xb - xa, r.height))
            if (a.oscuro) drawRect(a.filo(0.06f + 0.6f * k), Offset(xa, r.top - fondo), Size(xb - xa, 1.1f * u))
        }
        clipRect(r.left, r.top, r.right, r.bottom) {
            vetas(r, ROBLE_OSC.copy(alpha = 0.45f), semilla, densidad = 0.6f, grosor = 0.7f)
            // Las molduras de arriba y de abajo, y el friso entre ellas.
            drawRect(a.claro(0.28f), Offset(r.left, r.top + 2.2f * u), Size(r.width, 0.8f * u))
            drawRect(a.sombra(0.3f), Offset(r.left, r.top + 3.2f * u), Size(r.width, 0.8f * u))
            drawRect(a.sombra(0.3f), Offset(r.left, r.bottom - 4f * u), Size(r.width, 0.8f * u))
            drawRect(a.claro(0.2f), Offset(r.left, r.bottom - 3.2f * u), Size(r.width, 0.7f * u))
            val rf = r.height * 0.19f
            var x = r.left + 9f * u
            while (x < r.right - 4f * u) {
                val k = cerca(x)
                val c = Offset(x, r.center.y)
                for (o in listOf(Offset(0f, -1f), Offset(1f, 0f), Offset(0f, 1f), Offset(-1f, 0f))) {
                    val p = c + o * (rf * 0.55f)
                    drawCircle(a.sombra(0.45f), rf * 0.5f, p + Offset(0.8f * u, 0.9f * u))
                    drawCircle(a.luz(mezcla(ROBLE, ROBLE_LUZ, 0.35f), k), rf * 0.5f, p)
                }
                drawCircle(if (a.oscuro) a.filo(0.4f * k) else a.claro(0.3f), rf * 0.22f, c - Offset(rf * 0.25f, rf * 0.25f))
                drawCircle(a.sombra(0.3f), rf * 0.2f, c + Offset(rf * 0.1f, rf * 0.1f))
                x += 16f * u
            }
            // Una mella del uso.
            val mx = r.left + r.width * (0.15f + 0.7f * azar(semilla, 9))
            drawPath(poligono(mx, r.top, mx + 4f * u, r.top, mx + 1.5f * u, r.top + 2.6f * u), a.sombra(0.45f))
        }
        if (!a.oscuro) drawRect(a.claro(0.45f), r.topLeft, Size(r.width, 1f * u))
        drawLine(a.sombra(0.4f), Offset(r.left, r.top - fondo), Offset(r.right, r.top - fondo), 0.7f * u)
        drawRect(a.sombra(0.42f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
    }

    /** El rótulo de un mes: una filacteria de vitela con las puntas en cola de golondrina, clavada al canto con dos clavos. */
    private fun DrawScope.filacteria(p: PlacaEstante, medidor: TextMeasurer, a: Aire, k: Float) {
        val u = density
        val r = p.r
        val m = r.height * 0.55f
        val cinta = Path().apply {
            moveTo(r.left - m, r.top + 0.6f * u); lineTo(r.right + m, r.top); lineTo(r.right + m * 0.45f, r.center.y); lineTo(r.right + m, r.bottom)
            lineTo(r.left - m, r.bottom + 0.4f * u); lineTo(r.left - m * 0.45f, r.center.y); close()
        }
        drawPath(corrido(cinta, 1.4f * u, 1.8f * u), a.sombra(0.42f))
        drawPath(cinta, a.etiqueta(maxOf(k, 0.15f)))
        clipPath(cinta) {
            pinceladas(Rect(r.left - m, r.top, r.right + m, r.bottom), a.sombra(1f), 881 + p.texto.length, 8, u, 0.06f)
            // Los dobleces de las puntas, en sombra.
            drawRect(a.sombra(0.2f), Offset(r.left - m, r.top), Size(m, r.height + 1f * u))
            drawRect(a.sombra(0.2f), Offset(r.right, r.top), Size(m, r.height + 1f * u))
            drawRect(a.claro(0.5f), Offset(r.left - m, r.top), Size(r.width + 2 * m, 1f * u))
            drawRect(a.sombra(0.14f), Offset(r.left - m, r.bottom - 2f * u), Size(r.width + 2 * m, 2f * u))
        }
        val roja = if (a.oscuro) mezcla(GRANA, a.hondo, 0.2f) else GRANA
        drawLine(roja.copy(alpha = 0.7f), Offset(r.left + 8f * u, r.top + 2.4f * u), Offset(r.right - 8f * u, r.top + 2.2f * u), 0.7f * u)
        drawLine(roja.copy(alpha = 0.7f), Offset(r.left + 8f * u, r.bottom - 2.4f * u), Offset(r.right - 8f * u, r.bottom - 2.2f * u), 0.7f * u)
        listOf(r.left + 3.5f * u, r.right - 3.5f * u).forEach { x -> clavo(Offset(x, r.center.y), 1.4f * u, a.luz(HIERRO_LUZ, maxOf(k, 0.3f)), a, k) }
        textoEn(medidor, p.texto, Rect(r.left + 8f * u, r.top + 2.5f * u, r.right - 8f * u, r.bottom - 2.5f * u), LETRA, a.tinta, 11f, minimo = 5f)
    }

    /**
     * Un costado: el montante de roble con su contrafuerte aplicado (taludes en cada repisa y un gabletillo con su trébol),
     * las bisagras de hierro de las puertas que ya no están y, en el derecho, el pasador con su candado.
     */
    private fun DrawScope.costado(r: Rect, derecho: Boolean, d: DisposicionEstante, a: Aire, repisas: Int, ch: Float, fila: Float) {
        val u = density; val w = size.width
        val fx = r.center.x / w
        fun k(y: Float) = cercaCuerpo((y - ch) / maxOf(1f, fila), repisas, fx)
        val tramos = if (a.lumbre) (r.height / maxOf(1f, fila * 0.25f)).toInt().coerceIn(4, 80) else 1
        for (t in 0 until tramos) {
            val ya = r.top + r.height * t / tramos; val yb = r.top + r.height * (t + 1) / tramos + 0.6f
            val kk = k((ya + yb) / 2)
            drawRect(a.luz(ROBLE, kk), Offset(r.left, ya), Size(r.width, yb - ya))
            // El contrafuerte, que sobresale: más claro en su cara.
            drawRect(a.luz(mezcla(ROBLE, ROBLE_LUZ, 0.3f), kk), Offset(r.left + r.width * 0.2f, ya), Size(r.width * 0.6f, yb - ya))
        }
        clipRect(r.left, r.top, r.right, r.bottom) {
            vetas(r, ROBLE_OSC.copy(alpha = 0.45f), if (derecho) 841 else 842, densidad = 0.8f, vertical = true, grosor = 0.7f)
        }
        // El contrafuerte: su arista de luz y su lado de sombra, que cae en el montante.
        drawRect(a.claro(0.3f), Offset(r.left + r.width * 0.2f, r.top), Size(1.2f * u, r.height))
        drawRect(a.sombra(0.34f), Offset(r.left + r.width * 0.8f, r.top), Size(r.width * 0.12f, r.height))
        drawRect(a.sombra(0.2f), Offset(r.left + r.width * 0.72f, r.top), Size(r.width * 0.08f, r.height))
        // El rebajo hacia las repisas y la arista de fuera.
        if (derecho) drawRect(a.sombra(0.4f), Offset(r.left, r.top), Size(2f * u, r.height)) else drawRect(a.sombra(0.3f), Offset(r.right - 2f * u, r.top), Size(2f * u, r.height))
        if (!derecho) drawRect(a.claro(0.24f), r.topLeft, Size(1.2f * u, r.height))
        // Los taludes y los gabletillos, a media repisa.
        d.repisas.forEach { rp ->
            val y = rp.top + rp.height * 0.55f
            val kk = k(y)
            val x0 = r.left + r.width * 0.2f; val x1 = r.left + r.width * 0.8f
            val gab = poligono(x0, y, (x0 + x1) / 2, y - r.width * 0.62f, x1, y)
            drawPath(corrido(gab, 1.2f * u, 1.6f * u), a.sombra(0.35f))
            drawPath(gab, a.luz(mezcla(ROBLE, ROBLE_LUZ, 0.55f), kk))
            drawPath(poligono((x0 + x1) / 2, y - r.width * 0.62f, x1, y, (x0 + x1) / 2, y), a.sombra(0.22f))
            drawCircle(a.sombra(0.5f), r.width * 0.08f, Offset((x0 + x1) / 2, y - r.width * 0.2f))
            // El talud: la cara de arriba en luz y su sombra debajo.
            drawRect(a.luz(mezcla(ROBLE, ROBLE_LUZ, 0.7f), kk), Offset(x0 - 1f * u, y), Size(x1 - x0 + 2f * u, 2.4f * u))
            drawRect(a.sombra(0.4f), Offset(x0, y + 2.4f * u), Size(x1 - x0, 1.6f * u))
            if (a.oscuro) drawRect(a.filo(0.5f * kk), Offset(x0 - 1f * u, y), Size(x1 - x0 + 2f * u, 0.9f * u))
        }
        // Las bisagras de las puertas que ya no están: una en lo alto y otra abajo, con su gozne y sus clavos.
        val ys = listOf(r.top + 16f * u, r.bottom - 26f * u)
        ys.forEach { y ->
            val kk = k(y)
            val hacia = if (derecho) -1f else 1f
            val xg = if (derecho) r.left + 1.5f * u else r.right - 1.5f * u
            val largo = r.width * 0.55f
            val pala = poligono(xg, y - 2.2f * u, xg - hacia * largo, y - 1.6f * u, xg - hacia * (largo + 3f * u), y, xg - hacia * largo, y + 1.6f * u, xg, y + 2.2f * u)
            drawPath(corrido(pala, 1f * u, 1.2f * u), a.sombra(0.45f))
            drawPath(pala, a.luz(HIERRO, kk))
            drawRect(a.luz(HIERRO_LUZ, kk), Offset(xg - 1.6f * u, y - 5f * u), Size(3.2f * u, 10f * u))
            drawRect(a.claro(0.3f), Offset(xg - 1.6f * u, y - 5f * u), Size(0.8f * u, 10f * u))
            clavo(Offset(xg - hacia * largo * 0.55f, y), 0.9f * u, a.luz(HIERRO_LUZ, kk), a, kk)
        }
        // El pasador con su candado, en el costado derecho, a la altura de la segunda repisa.
        if (derecho && d.repisas.size > 1) {
            val y = d.repisas[1].top + 20f * u
            val kk = k(y)
            drawRect(a.sombra(0.4f), Offset(r.left + 1f * u, y + 1f * u), Size(r.width * 0.8f, 3f * u))
            drawRect(a.luz(HIERRO, kk), Offset(r.left, y), Size(r.width * 0.8f, 3f * u))
            drawRect(a.claro(0.3f), Offset(r.left, y), Size(r.width * 0.8f, 0.7f * u))
            val c = Offset(r.left + r.width * 0.42f, y + 9f * u)
            drawArc(a.luz(HIERRO_LUZ, kk), 180f, 180f, false, Offset(c.x - 3f * u, c.y - 7f * u), Size(6f * u, 7f * u), style = Stroke(1.3f * u))
            val cuerpo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(c.x - 4.2f * u, c.y - 3.5f * u, c.x + 4.2f * u, c.y + 4.5f * u), CornerRadius(1.5f * u))) }
            drawPath(corrido(cuerpo, 1.2f * u, 1.6f * u), a.sombra(0.45f))
            drawPath(cuerpo, a.luz(HIERRO, kk))
            drawPath(borde(cuerpo, 1f * u, 1f * u), a.claro(0.3f))
            drawCircle(a.sombra(0.6f), 0.9f * u, c)
        }
    }

    /** La cornisa: su cara de arriba, el canto con su friso de arquillos ciegos y la moldura. */
    private fun DrawScope.cornisa(r: Rect, fondo: Float, a: Aire, cerca: (Float) -> Float) {
        val u = density
        val tramos = if (a.lumbre) 30 else 1
        for (t in 0 until tramos) {
            val xa = r.left + r.width * t / tramos; val xb = r.left + r.width * (t + 1) / tramos + 0.6f
            val k = cerca((xa + xb) / 2)
            drawRect(a.luz(mezcla(ROBLE, ROBLE_LUZ, 0.55f), k), Offset(xa, r.top - fondo), Size(xb - xa, fondo))
            drawRect(a.luz(ROBLE, k), Offset(xa, r.top), Size(xb - xa, r.height))
            if (a.oscuro) drawRect(a.filo(0.08f + 0.6f * k), Offset(xa, r.top - fondo), Size(xb - xa, 1.2f * u))
        }
        clipRect(r.left, r.top - fondo, r.right, r.bottom) {
            vetas(Rect(r.left, r.top - fondo, r.right, r.top), ROBLE_OSC.copy(alpha = 0.3f), 851, densidad = 0.9f, grosor = 0.5f)
            vetas(r, ROBLE_OSC.copy(alpha = 0.4f), 852, densidad = 0.6f, grosor = 0.7f)
            // La moldura: el bocel de arriba con luz y la escocia en sombra.
            drawRect(a.claro(0.3f), Offset(r.left, r.top + 1.5f * u), Size(r.width, 1.4f * u))
            drawRect(a.sombra(0.35f), Offset(r.left, r.top + 3.4f * u), Size(r.width, 1.4f * u))
            // El friso de arquillos ciegos.
            val paso = 13f * u
            var x = r.left + 4f * u
            while (x + paso < r.right) {
                val ar = Rect(x + 1.5f * u, r.top + 6.5f * u, x + paso - 1.5f * u, r.bottom - 3.5f * u)
                val k = cerca(ar.center.x)
                val p = ojiva(ar, ar.width * 0.9f)
                drawPath(p, a.luz(ROBLE_OSC, k))
                drawPath(borde(p, 1.6f * u, 1.6f * u), a.sombra(0.4f))
                drawPath(borde(p, -1f * u, -1f * u), if (a.oscuro) a.filo(0.4f * k) else a.claro(0.24f))
                x += paso
            }
            drawRect(a.sombra(0.4f), Offset(r.left, r.bottom - 2.4f * u), Size(r.width, 2.4f * u))
        }
        drawLine(a.sombra(0.4f), Offset(r.left, r.top - fondo), Offset(r.right, r.top - fondo), 0.7f * u)
    }

    /** Un pináculo de roble sobre el costado: el fuste con su luz y su sombra, la aguja con sus crochets y el florón. */
    private fun DrawScope.pinaculo(pie: Offset, an: Float, alto: Float, a: Aire, k: Float, caida: Offset) {
        val u = density
        val fuste = alto * 0.42f
        val sil = Path().apply {
            addRect(Rect(pie.x - an / 2, pie.y - fuste, pie.x + an / 2, pie.y))
            addPath(poligono(pie.x - an * 0.6f, pie.y - fuste, pie.x, pie.y - alto, pie.x + an * 0.6f, pie.y - fuste))
        }
        translate(caida.x, caida.y) { drawPath(sil, a.sombra(if (a.oscuro) 0.38f else 0.2f)) }
        drawRect(a.luz(ROBLE, k), Offset(pie.x - an / 2, pie.y - fuste), Size(an, fuste))
        drawRect(a.claro(0.28f), Offset(pie.x - an / 2, pie.y - fuste), Size(an * 0.18f, fuste))
        drawRect(a.sombra(0.32f), Offset(pie.x + an * 0.18f, pie.y - fuste), Size(an * 0.32f, fuste))
        // Un arquillo ciego en el fuste.
        val ar = Rect(pie.x - an * 0.24f, pie.y - fuste * 0.85f, pie.x + an * 0.12f, pie.y - fuste * 0.18f)
        drawPath(ojiva(ar, ar.width), a.sombra(0.45f))
        // El tejadillo que remata el fuste.
        drawRect(a.luz(ROBLE_LUZ, k), Offset(pie.x - an * 0.62f, pie.y - fuste - 2f * u), Size(an * 1.24f, 2.6f * u))
        drawRect(a.sombra(0.4f), Offset(pie.x - an * 0.62f, pie.y - fuste + 0.6f * u), Size(an * 1.24f, 1f * u))
        val aguja = poligono(pie.x - an * 0.5f, pie.y - fuste - 2f * u, pie.x, pie.y - alto, pie.x + an * 0.5f, pie.y - fuste - 2f * u)
        drawPath(aguja, a.luz(mezcla(ROBLE, ROBLE_LUZ, 0.4f), k))
        drawPath(poligono(pie.x, pie.y - alto, pie.x + an * 0.5f, pie.y - fuste - 2f * u, pie.x, pie.y - fuste - 2f * u), a.sombra(0.3f))
        // Los crochets: hojas enroscadas que suben por las aristas.
        for (j in 1..4) {
            val f = j / 5f
            val yy = pie.y - fuste - (alto - fuste) * f
            val dx = an * 0.5f * (1f - f)
            drawCircle(a.luz(ROBLE_LUZ, k), an * 0.12f, Offset(pie.x - dx - an * 0.08f, yy))
            drawCircle(a.claro(0.3f), an * 0.05f, Offset(pie.x - dx - an * 0.11f, yy - an * 0.03f))
            drawCircle(a.luz(ROBLE, k), an * 0.12f, Offset(pie.x + dx + an * 0.08f, yy))
            drawCircle(a.sombra(0.3f), an * 0.12f, Offset(pie.x + dx + an * 0.08f, yy))
        }
        // El florón, con su dorado gastado.
        val fl = Offset(pie.x, pie.y - alto - an * 0.1f)
        drawCircle(a.luz(ORO, k), an * 0.17f, fl)
        drawCircle(a.luz(ORO, k), an * 0.11f, fl + Offset(-an * 0.17f, an * 0.1f))
        drawCircle(a.luz(ORO, k), an * 0.11f, fl + Offset(an * 0.17f, an * 0.1f))
        drawCircle(a.claro(0.5f), an * 0.06f, fl - Offset(an * 0.05f, an * 0.05f))
        if (a.oscuro) drawLine(a.filo(0.6f * k), Offset(pie.x - an / 2, pie.y - fuste), Offset(pie.x - an / 2, pie.y), 1f * u)
    }

    /** El zócalo: la caja de roble que pisa las losas, con su arquería ciega, el zócalo gastado y su sombra. */
    private fun DrawScope.zocalo(r: Rect, a: Aire, cerca: (Offset) -> Float) {
        val u = density
        if (r.height < 2f) return
        val k = cerca(r.center)
        drawRect(a.luz(ROBLE, k), r.topLeft, r.size)
        vetas(r, ROBLE_OSC.copy(alpha = 0.45f), 871, densidad = 0.7f, grosor = 0.7f)
        drawRect(a.luz(ROBLE_LUZ, k), r.topLeft, Size(r.width, 3f * u))
        drawRect(a.sombra(0.35f), Offset(r.left, r.top + 3f * u), Size(r.width, 1.2f * u))
        val paso = 22f * u
        var x = r.left + 8f * u
        while (x + paso < r.right - 4f * u) {
            val ar = Rect(x + 2f * u, r.top + 9f * u, x + paso - 2f * u, r.bottom - 7f * u)
            if (ar.height > 4f * u) {
                val p = ojiva(ar, ar.width * 0.9f)
                drawPath(p, a.luz(ROBLE_OSC, k))
                drawPath(borde(p, 2f * u, 2f * u), a.sombra(0.4f))
                drawPath(borde(p, -1.2f * u, -1.2f * u), a.claro(0.2f))
            }
            x += paso
        }
        // El pie, comido por el fregado.
        drawRect(a.luz(mezcla(ROBLE, ROBLE_OSC, 0.4f), k), Offset(r.left, r.bottom - 5f * u), Size(r.width, 5f * u))
        pinceladas(Rect(r.left, r.bottom - 6f * u, r.right, r.bottom), a.luz(CALIZA_OSC, k), 872, 16, u, 0.14f)
        drawRect(a.sombra(0.3f), Offset(r.right - 10f * u, r.top), Size(10f * u, r.height))
    }

    // ---------------------------------------------------------------- lo de arriba

    /**
     * El farol de cuerno colgado de su pescante de hierro: la placa clavada en la piedra, el brazo con su voluta, el gancho, la
     * anilla, el tejadillo cónico calado, el cuerpo de láminas de cuerno con sus flejes y la vela de sebo dentro. De noche
     * el cuerno arde por dentro (en los Sueños, vidrios de colores).
     */
    private fun DrawScope.farol(a: Aire, noche: Boolean) {
        val u = density; val w = size.width
        val cx = w * LAMP_X
        val k = if (a.lumbre) 1f else 0.6f
        val hierro = a.luz(HIERRO, k)
        val placa = Offset(w * 0.255f, w * 0.17f)
        // El pescante: la placa con dos clavos, el brazo y la voluta de debajo.
        drawRect(a.sombra(0.4f), Offset(placa.x - 2f * u, placa.y - 7f * u), Size(6f * u, 16f * u))
        drawRect(hierro, Offset(placa.x - 3f * u, placa.y - 8f * u), Size(6f * u, 16f * u))
        clavo(Offset(placa.x, placa.y - 5f * u), 1f * u, a.luz(HIERRO_LUZ, k), a, k)
        clavo(Offset(placa.x, placa.y + 5f * u), 1f * u, a.luz(HIERRO_LUZ, k), a, k)
        drawLine(a.sombra(0.3f), Offset(placa.x + 2f * u, placa.y + 3f * u), Offset(cx + 2f * u, placa.y + 3f * u), 2.4f * u)
        drawLine(hierro, Offset(placa.x, placa.y), Offset(cx - 2f * u, placa.y), 2.4f * u, StrokeCap.Round)
        val voluta = Path().apply {
            moveTo(placa.x - 2f * u, placa.y + 6f * u)
            cubicTo(placa.x - w * 0.03f, placa.y + 7f * u, placa.x - w * 0.06f, placa.y + 4f * u, placa.x - w * 0.055f, placa.y + 1f * u)
            cubicTo(placa.x - w * 0.05f, placa.y - 1f * u, placa.x - w * 0.04f, placa.y + 2f * u, placa.x - w * 0.044f, placa.y + 3f * u)
        }
        drawPath(voluta, hierro, style = Stroke(1.4f * u, cap = StrokeCap.Round))
        drawLine(a.claro(0.3f), Offset(placa.x, placa.y - 1f * u), Offset(cx, placa.y - 1f * u), 0.6f * u)
        // El gancho y la anilla.
        drawArc(hierro, 0f, 300f, false, Offset(cx - 2.6f * u, placa.y - 1f * u), Size(5.2f * u, 6f * u), style = Stroke(1.2f * u))
        val top = w * 0.19f
        drawCircle(hierro, 2.6f * u, Offset(cx, top - 1f * u), style = Stroke(1.2f * u))
        // El tejadillo cónico, calado.
        val an = w * 0.082f
        val c0 = top + 2f * u; val c1 = w * 0.222f
        val techo = poligono(cx - 1.5f * u, c0, cx + 1.5f * u, c0, cx + an * 0.6f, c1, cx - an * 0.6f, c1)
        drawPath(corrido(techo, w * 0.02f, w * 0.012f), a.sombra(if (a.oscuro) 0.3f else 0.2f))
        drawPath(techo, a.luz(HIERRO, k))
        drawPath(poligono(cx, c0, cx + an * 0.6f, c1, cx, c1), a.sombra(0.3f))
        for (j in 0 until 3) {
            val yy = c0 + (c1 - c0) * (0.4f + 0.2f * j)
            val half = (an * 0.6f) * (yy - c0) / (c1 - c0)
            for (q in -1..1) drawCircle(if (a.lumbre) FUEGO.copy(alpha = 0.8f) else a.sombra(0.5f), 0.8f * u, Offset(cx + q * half * 0.5f, yy))
        }
        drawRect(a.luz(HIERRO_LUZ, k), Offset(cx - an * 0.62f, c1 - 1f * u), Size(an * 1.24f, 2.2f * u))
        // El cuerpo: cuatro láminas de cuerno vistas de frente (tres), con los flejes y la base.
        val b0 = c1 + 1.2f * u; val b1 = w * 0.335f
        val cuerpo = Rect(cx - an / 2, b0, cx + an / 2, b1)
        drawRect(a.sombra(if (a.oscuro) 0.3f else 0.2f), cuerpo.topLeft + Offset(w * 0.022f, w * 0.014f), cuerpo.size)
        val paneles = listOf(Rect(cuerpo.left, b0, cuerpo.left + an * 0.22f, b1), Rect(cuerpo.left + an * 0.22f, b0, cuerpo.right - an * 0.22f, b1), Rect(cuerpo.right - an * 0.22f, b0, cuerpo.right, b1))
        paneles.forEachIndexed { i, p ->
            val color = when {
                noche -> mezcla(VIDRIOS[(i + 1) % VIDRIOS.size], FUEGO, if (a.oscuro) 0.2f else 0.3f)
                a.lumbre -> mezcla(CUERNO, FUEGO, if (i == 1) 0.55f else 0.25f)
                else -> mezcla(CUERNO, CALIZA_OSC, 0.25f + 0.15f * i)
            }
            drawRect(color, p.topLeft, p.size)
            // Las vetas del cuerno.
            for (j in 0 until 3) drawLine((if (a.lumbre) Color(0xFFB07020) else a.luz(CUERO, 0.7f)).copy(alpha = 0.3f), Offset(p.left + p.width * (0.2f + 0.3f * j), p.top), Offset(p.left + p.width * (0.25f + 0.3f * j), p.bottom), 0.7f * u)
        }
        if (!a.lumbre) {
            // Apagado: la vela se adivina por dentro, y el cuerno toma la luz por la izquierda.
            drawRect(a.luz(SEBO, 0.6f).copy(alpha = 0.5f), Offset(cx - 2.6f * u, b1 - (b1 - b0) * 0.55f), Size(5.2f * u, (b1 - b0) * 0.5f))
            drawRect(a.claro(0.3f), Offset(cuerpo.left, b0), Size(an * 0.12f, b1 - b0))
            drawRect(a.sombra(0.25f), Offset(cuerpo.right - an * 0.22f, b0), Size(an * 0.22f, b1 - b0))
        } else {
            drawRect(a.sombra(0.2f), Offset(cuerpo.right - an * 0.22f, b0), Size(an * 0.22f, b1 - b0))
        }
        for (x in listOf(cuerpo.left, cuerpo.left + an * 0.22f, cuerpo.right - an * 0.22f, cuerpo.right)) {
            drawLine(a.luz(HIERRO, k), Offset(x, b0), Offset(x, b1), 1.6f * u)
        }
        drawLine(a.luz(HIERRO, k), Offset(cuerpo.left, (b0 + b1) / 2), Offset(cuerpo.right, (b0 + b1) / 2), 1.1f * u)
        drawRect(a.luz(HIERRO, k), Offset(cuerpo.left - 1.5f * u, b1 - 0.5f * u), Size(an + 3f * u, 3.2f * u))
        drawRect(a.claro(0.3f), Offset(cuerpo.left - 1.5f * u, b1 - 0.5f * u), Size(an + 3f * u, 0.7f * u))
        // La puertecilla con su pestillo, a la derecha.
        drawCircle(a.luz(HIERRO_LUZ, k), 1.1f * u, Offset(cuerpo.right - an * 0.3f, (b0 + b1) / 2 + 5f * u))
    }

    /** La llama de la vela dentro del farol, que se ve a través del cuerno: su color propio. */
    private fun DrawScope.llamaFarol(a: Aire, noche: Boolean) {
        val w = size.width
        val p = Offset(w * LAMP_X, w * LAMP_Y + w * 0.02f)
        val s = w * 0.03f
        val f = if (a.oscuro) 1f else 0.7f
        drawCircle(FUEGO.copy(alpha = 0.3f * f), s * 1.1f, p - Offset(0f, s * 0.4f))
        val lengua = Path().apply {
            moveTo(p.x - s * 0.22f, p.y)
            cubicTo(p.x - s * 0.34f, p.y - s * 0.4f, p.x - s * 0.04f, p.y - s * 0.7f, p.x + s * 0.03f, p.y - s * 1.15f)
            cubicTo(p.x + s * 0.14f, p.y - s * 0.62f, p.x + s * 0.34f, p.y - s * 0.36f, p.x + s * 0.22f, p.y)
            close()
        }
        drawPath(lengua, (if (noche) Color(0xFFFFB050) else Color(0xFFFF9A2E)).copy(alpha = 0.9f * f))
        drawCircle(Color(0xFFFFF6DC).copy(alpha = 0.9f * f), s * 0.16f, p - Offset(0f, s * 0.22f))
        // La vela, debajo de la llama.
        drawRect(mezcla(SEBO, FUEGO, 0.4f).copy(alpha = 0.85f), Offset(p.x - s * 0.18f, p.y + s * 0.05f), Size(s * 0.36f, s * 1.3f))
    }

    /** El atado de velas de sebo colgado de un clavo en el fondo del nicho: cinco velas colgadas por sus mechas anudadas. */
    private fun DrawScope.velasColgadas(clavo: Offset, w: Float, a: Aire, k: Float, caida: Offset) {
        val u = density
        val nudo = clavo + Offset(0f, w * 0.018f)
        val paso = w * 0.016f
        val largos = listOf(0.085f, 0.1f, 0.094f, 0.078f, 0.09f)
        for (pasada in 0..1) {
            for (j in 0 until 5) {
                val x = nudo.x + (j - 2) * paso
                val top = nudo.y + w * 0.022f + w * 0.004f * azar(903, j)
                val r = Rect(x - w * 0.0055f, top, x + w * 0.0055f, top + w * largos[j])
                if (pasada == 0) {
                    drawRoundRect(a.sombra(if (a.oscuro) 0.16f else 0.14f), r.topLeft + caida * 1.8f, r.size, CornerRadius(1.5f * u))
                } else {
                    // La mecha que sube al nudo.
                    drawLine(a.luz(mezcla(CUERNO, TINTA, 0.4f), k), Offset(x, top), nudo, 0.7f * u)
                    drawRoundRect(a.luz(mezcla(SEBO, CUERNO, 0.2f * azar(902, j)), k), r.topLeft, r.size, CornerRadius(1.5f * u))
                    drawRect(a.claro(0.4f), Offset(r.left + 0.5f * u, r.top + 1.5f * u), Size(1f * u, r.height - 3f * u))
                    drawRect(a.sombra(0.22f), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
                    // El cabo de abajo, redondeado y algo chorreado.
                    drawOval(a.luz(SEBO, k), Offset(r.left - 0.4f * u, r.bottom - 2f * u), Size(r.width + 0.8f * u, 3f * u))
                    if (a.oscuro) drawRect(a.filo(0.5f * k), Offset(r.left, r.top), Size(0.8f * u, r.height))
                }
            }
        }
        drawLine(a.luz(CUERO, k), clavo, nudo, 0.9f * u)
        drawCircle(a.luz(mezcla(CUERNO, CUERO, 0.5f), k), 1.8f * u, nudo)
        drawCircle(a.sombra(0.4f), 1.4f * u, clavo + Offset(0.5f * u, 0.5f * u))
        drawCircle(a.luz(HIERRO_LUZ, k), 1.2f * u, clavo)
    }

    /** La escuadra de hierro y el compás de madera del cantero, apoyados contra el pináculo y la piedra, cortados por el borde. */
    private fun DrawScope.utilesCantero(pie: Offset, w: Float, a: Aire, k: Float, caida: Offset) {
        val u = density
        // La escuadra: el brazo largo apoyado en la piedra, un poco ladeado, y el corto que pisa la cornisa.
        val e0 = pie + Offset(w * 0.035f, -w * 0.006f)
        val e1 = e0 + Offset(w * 0.05f, -w * 0.215f)
        val e2 = e0 + Offset(-w * 0.085f, -w * 0.004f)
        val g = w * 0.014f
        for (pasada in 0..1) {
            val dx = if (pasada == 0) caida.x * 2.4f else 0f; val dy = if (pasada == 0) caida.y * 2.4f else 0f
            val col = if (pasada == 0) a.sombra(if (a.oscuro) 0.36f else 0.22f) else a.luz(mezcla(HIERRO, HIERRO_LUZ, 0.45f), k)
            drawLine(col, e0 + Offset(dx, dy), e1 + Offset(dx, dy), g, StrokeCap.Butt)
            drawLine(col, e0 + Offset(dx, dy), e2 + Offset(dx, dy), g, StrokeCap.Butt)
        }
        drawLine(a.luz(HIERRO_LUZ, k), e0 + Offset(-g * 0.4f, 0f), e1 + Offset(-g * 0.4f, 0f), 0.8f * u)
        drawLine(a.luz(HIERRO_LUZ, k), e0 + Offset(0f, -g * 0.4f), e2 + Offset(0f, -g * 0.4f), 0.8f * u)
        // Las marcas de medida, a golpe de cincel.
        for (j in 1..7) {
            val p = e0 + (e1 - e0) * (j / 8f)
            drawLine(a.claro(0.4f).let { if (a.oscuro) a.filo(0.4f * k) else it }, p, p + Offset(-g * 0.45f, -g * 0.15f), 0.6f * u)
        }
        if (a.oscuro) drawLine(a.filo(0.5f * k), e0 + Offset(-g * 0.5f, 0f), e1 + Offset(-g * 0.5f, 0f), 0.8f * u)
        // El compás de madera: dos patas abiertas con su clavija y sus puntas de hierro.
        val cab = pie + Offset(-w * 0.035f, -w * 0.2f)
        val p1 = pie + Offset(-w * 0.07f, 0f); val p2 = pie + Offset(w * 0.0f, 0f)
        drawLine(a.sombra(if (a.oscuro) 0.36f else 0.2f), cab + caida * 2.4f, p2 + Offset(caida.x * 2f, 0f), 3f * u)
        for (q in listOf(p1, p2)) {
            drawLine(a.luz(mezcla(ROBLE_LUZ, CUERO_BLANCO, 0.3f), k), cab, q - (q - cab) * 0.08f, 3.2f * u, StrokeCap.Round)
            drawLine(a.claro(0.35f), cab + Offset(-1f * u, 0f), q - (q - cab) * 0.08f + Offset(-1f * u, 0f), 0.8f * u)
            drawLine(a.luz(HIERRO_LUZ, k), q - (q - cab) * 0.08f, q, 1.4f * u)
        }
        drawCircle(a.luz(ROBLE_LUZ, k), 3.6f * u, cab)
        clavo(cab, 1.4f * u, a.luz(HIERRO_LUZ, k), a, k)
        contacto(Offset(pie.x - w * 0.03f, pie.y), w * 0.12f, w * 0.008f, a, 0.3f)
    }

    /**
     * La arqueta-relicario de esmalte de Limoges: la caja de cobre dorado sobre cuatro patas, el frente y el tejadillo a dos
     * aguas de esmalte azul con medallones de cuadrifolio y vermiculado de oro, las orlas con cabujones, la crestería calada
     * con sus tres pomos y el testero derecho en sombra. Es lo que primero se ve.
     */
    private fun DrawScope.arqueta(pie: Offset, an: Float, a: Aire, k: Float, caida: Offset, suenos: Boolean) {
        val u = density
        val patas = an * 0.06f
        val cuerpoAlto = an * 0.4f
        val techoAlto = an * 0.26f
        val prof = an * 0.1f                                  // lo que se ve del testero derecho
        val x0 = pie.x - an / 2; val x1 = pie.x + an / 2
        val yb = pie.y - patas; val yt = yb - cuerpoAlto
        val yc = yt - techoAlto
        // El brillo propio del esmalte en los Sueños.
        val brilla = suenos
        fun esm(c: Color) = if (brilla) mezcla(a.luz(c, maxOf(k, 0.5f)), c, 0.4f) else a.luz(c, k)
        val sil = Path().apply {
            addRect(Rect(x0, yt, x1 + prof, pie.y))
            addPath(poligono(x0 - an * 0.02f, yt, x0 + an * 0.1f, yc, x1 - an * 0.1f + prof, yc - prof * 0.4f, x1 + prof + an * 0.02f, yt))
        }
        translate(caida.x * 2.2f, caida.y * 2.2f) { drawPath(sil, a.sombra(if (a.oscuro) 0.4f else 0.22f)) }
        contacto(Offset(pie.x + an * 0.06f, pie.y), an * 1.15f, an * 0.05f, a, 0.4f)
        // Las patas.
        for (x in listOf(x0 + an * 0.04f, x1 - an * 0.1f, x1 + prof - an * 0.04f)) {
            drawRect(a.luz(ORO, k), Offset(x, yb), Size(an * 0.06f, patas))
            drawRect(a.sombra(0.3f), Offset(x + an * 0.035f, yb), Size(an * 0.025f, patas))
        }
        // El testero derecho, en sombra, y el faldón del tejado de ese lado.
        drawPath(poligono(x1, yt, x1 + prof, yt - prof * 0.4f, x1 + prof, yb - prof * 0.4f, x1, yb), mezcla(esm(ESMALTE), a.hondo, 0.4f))
        drawPath(poligono(x1, yt, x1 + prof, yt - prof * 0.4f, x1 + prof, yb - prof * 0.4f, x1, yb), a.luz(ORO, k * 0.6f), style = Stroke(1.2f * u))
        // El faldón del tejado de ese lado (tejado de cuatro aguas visto un poco de lado), en sombra.
        val faldon = poligono(x1 + an * 0.02f, yt, x1 - an * 0.1f, yc, x1 - an * 0.1f + prof * 0.6f, yc - prof * 0.25f, x1 + prof + an * 0.01f, yt - prof * 0.4f)
        drawPath(faldon, mezcla(esm(ESMALTE), a.hondo, 0.5f))
        drawPath(faldon, a.luz(ORO, k * 0.6f), style = Stroke(1f * u))

        // El frente de la caja.
        val frente = Rect(x0, yt, x1, yb)
        drawRect(esm(ESMALTE), frente.topLeft, frente.size)
        // El tejadillo de frente.
        val techo = poligono(x0 - an * 0.02f, yt, x0 + an * 0.1f, yc, x1 - an * 0.1f, yc, x1 + an * 0.02f, yt)
        drawPath(techo, esm(mezcla(ESMALTE, TURQUESA, 0.2f)))
        // Los medallones: cuadrifolios de oro con un fondo turquesa y un botón grana, tres en la caja y dos en el tejado.
        fun medallon(c: Offset, r: Float) {
            for (o in listOf(Offset(0f, -1f), Offset(1f, 0f), Offset(0f, 1f), Offset(-1f, 0f))) drawCircle(a.luz(ORO, k), r * 0.55f, c + o * (r * 0.5f))
            for (o in listOf(Offset(0f, -1f), Offset(1f, 0f), Offset(0f, 1f), Offset(-1f, 0f))) drawCircle(esm(TURQUESA), r * 0.4f, c + o * (r * 0.5f))
            drawCircle(esm(GRANA), r * 0.26f, c)
            drawCircle(a.claro(0.5f), r * 0.1f, c - Offset(r * 0.08f, r * 0.08f))
        }
        val rm = cuerpoAlto * 0.26f
        for (j in 0 until 3) medallon(Offset(x0 + an * (0.2f + 0.3f * j), (yt + yb) / 2), rm)
        for (j in 0 until 2) medallon(Offset(x0 + an * (0.33f + 0.34f * j), (yt + yc) / 2 + 1f * u), techoAlto * 0.3f)
        // El vermiculado: zarcillos de oro entre los medallones.
        for (j in 0 until 4) {
            val c = Offset(x0 + an * (0.05f + 0.3f * j), (yt + yb) / 2)
            val p = Path().apply { moveTo(c.x, c.y + rm * 0.9f); cubicTo(c.x + rm * 0.5f, c.y + rm * 0.2f, c.x - rm * 0.4f, c.y - rm * 0.2f, c.x + rm * 0.1f, c.y - rm * 0.9f) }
            drawPath(p, a.luz(ORO, k).copy(alpha = 0.8f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        }
        // Las orlas de cobre dorado con sus cabujones.
        val oro = a.luz(ORO, k)
        drawRect(oro, Offset(x0, yt), Size(an, an * 0.035f))
        drawRect(oro, Offset(x0, yb - an * 0.035f), Size(an, an * 0.035f))
        drawRect(oro, Offset(x0, yt), Size(an * 0.03f, cuerpoAlto))
        drawRect(oro, Offset(x1 - an * 0.03f, yt), Size(an * 0.03f, cuerpoAlto))
        drawPath(techo, oro, style = Stroke(an * 0.022f))
        for (j in 0 until 7) {
            val c = Offset(x0 + an * (0.08f + 0.14f * j), yb - an * 0.018f)
            drawCircle(esm(if (j % 2 == 0) GRANA else ESMALTE), an * 0.012f, c)
        }
        // La crestería calada del caballete, con sus tres pomos de cristal.
        val cresta = Rect(x0 + an * 0.1f, yc - an * 0.05f, x1 - an * 0.1f, yc)
        drawRect(oro, Offset(cresta.left, cresta.bottom - an * 0.015f), Size(cresta.width, an * 0.015f))
        var cx = cresta.left
        while (cx < cresta.right - 1f) {
            drawArc(oro, 180f, 180f, false, Offset(cx, cresta.top + an * 0.012f), Size(an * 0.045f, an * 0.06f), style = Stroke(0.9f * u))
            cx += an * 0.045f
        }
        for (f in listOf(0f, 0.5f, 1f)) {
            val c = Offset(cresta.left + cresta.width * f, cresta.top - an * 0.012f)
            drawCircle(a.sombra(0.3f), an * 0.026f, c + Offset(1f * u, 1f * u))
            drawCircle(esm(mezcla(TURQUESA, LUNA, 0.3f)), an * 0.024f, c)
            drawCircle(a.claro(0.7f), an * 0.009f, c - Offset(an * 0.008f, an * 0.008f))
        }
        // La luz: la cara de la izquierda más clara, la derecha en sombra; el brillo del cobre en la arista; el filo del farol.
        drawRect(a.claro(0.1f), Offset(x0 + an * 0.03f, yt + an * 0.035f), Size(an * 0.06f, cuerpoAlto - an * 0.07f))
        drawRect(a.sombra(0.1f), Offset(x1 - an * 0.12f, yt + an * 0.035f), Size(an * 0.09f, cuerpoAlto - an * 0.07f))

        drawLine(a.claro(0.7f), Offset(x0 + 1f * u, yt + 1f * u), Offset(x0 + an * 0.5f, yt + 1f * u), 0.8f * u)
        if (a.oscuro || brilla) {
            val fk = if (brilla) maxOf(k, 0.6f) else k
            drawLine(a.filo(0.8f * fk), Offset(x0 - an * 0.02f, yt), Offset(x0 + an * 0.1f, yc), 1.2f * u)
            drawLine(a.filo(0.7f * fk), Offset(x0, yt), Offset(x0, yb), 1.2f * u)
            drawLine(a.filo(0.6f * fk), Offset(x0 + an * 0.1f, yc), Offset(x1 - an * 0.1f, yc), 1f * u)
            if (brilla) mancha(Offset(pie.x - an * 0.1f, (yt + yb) / 2), an * 0.5f, TURQUESA.copy(alpha = 0.18f))
        }
    }

    /** El reloj de arena: dos platos de madera torneada, tres balaustres y las dos ampollas, con la arena cayendo. */
    private fun DrawScope.relojArena(pie: Offset, alto: Float, a: Aire, k: Float, caida: Offset) {
        val u = density
        val an = alto * 0.5f
        val plato = alto * 0.08f
        val sil = Path().apply { addRect(Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)) }
        // Delante de la arqueta, su sombra caería sobre ella: solo la de contacto.
        if (caida != Offset.Zero) translate(caida.x * 2f, caida.y * 2f) { drawPath(sil, a.sombra(if (a.oscuro) 0.32f else 0.16f)) }
        contacto(Offset(pie.x + 2f * u, pie.y), an * 1.3f, alto * 0.06f, a)
        val y0 = pie.y - alto + plato; val y1 = pie.y - plato; val ym = (y0 + y1) / 2
        // Las ampollas de vidrio.
        val vidrio = Path().apply {
            moveTo(pie.x - an * 0.36f, y0)
            cubicTo(pie.x - an * 0.4f, ym - alto * 0.18f, pie.x - an * 0.06f, ym - alto * 0.06f, pie.x - an * 0.05f, ym)
            cubicTo(pie.x - an * 0.06f, ym + alto * 0.06f, pie.x - an * 0.4f, ym + alto * 0.18f, pie.x - an * 0.36f, y1)
            lineTo(pie.x + an * 0.36f, y1)
            cubicTo(pie.x + an * 0.4f, ym + alto * 0.18f, pie.x + an * 0.06f, ym + alto * 0.06f, pie.x + an * 0.05f, ym)
            cubicTo(pie.x + an * 0.06f, ym - alto * 0.06f, pie.x + an * 0.4f, ym - alto * 0.18f, pie.x + an * 0.36f, y0)
            close()
        }
        drawPath(vidrio, a.luz(mezcla(CALIZA, LUNA, 0.5f), k).copy(alpha = 0.35f))
        clipPath(vidrio) {
            // La arena: lo que queda arriba, el hilo y el montón de abajo.
            drawRect(a.luz(CUERNO, k), Offset(pie.x - an, ym - alto * 0.12f), Size(an * 2, alto * 0.12f))
            drawLine(a.luz(CUERNO, k), Offset(pie.x, ym), Offset(pie.x, y1), 0.8f * u)
            drawPath(poligono(pie.x - an * 0.4f, y1, pie.x, y1 - alto * 0.2f, pie.x + an * 0.4f, y1), a.luz(CUERNO, k))
            drawRect(a.sombra(0.18f), Offset(pie.x + an * 0.12f, y0), Size(an * 0.3f, y1 - y0))
        }
        drawPath(vidrio, a.claro(0.5f).let { if (a.oscuro) a.filo(0.5f * k) else it }, style = Stroke(0.7f * u))
        drawLine(a.claro(0.7f), Offset(pie.x - an * 0.27f, y0 + alto * 0.05f), Offset(pie.x - an * 0.2f, ym - alto * 0.12f), 1f * u)
        // Los balaustres.
        for (x in listOf(pie.x - an * 0.44f, pie.x + an * 0.44f)) {
            drawLine(a.luz(ROBLE_LUZ, k), Offset(x, y0), Offset(x, y1), 2.2f * u)
            drawCircle(a.luz(ROBLE_LUZ, k), 1.8f * u, Offset(x, ym))
        }
        drawLine(a.sombra(0.4f), Offset(pie.x + an * 0.44f + 1f * u, y0), Offset(pie.x + an * 0.44f + 1f * u, y1), 0.8f * u)
        // Los platos.
        for (y in listOf(pie.y - alto, y1)) {
            drawRoundRect(a.luz(ROBLE, k), Offset(pie.x - an / 2, y), Size(an, plato), CornerRadius(1.4f * u))
            drawRect(a.claro(0.3f), Offset(pie.x - an / 2, y), Size(an, 0.8f * u))
            drawRect(a.sombra(0.3f), Offset(pie.x + an * 0.3f, y), Size(an * 0.2f, plato))
        }
        if (a.oscuro) drawLine(a.filo(0.6f * k), Offset(pie.x - an / 2, pie.y - alto), Offset(pie.x + an * 0.2f, pie.y - alto), 1f * u)
    }

    /** El tintero de cuerno en su soporte de madera, con dos plumas de ganso y el cortaplumas delante. */
    private fun DrawScope.tintero(pie: Offset, an: Float, a: Aire, k: Float, caida: Offset) {
        val u = density
        val alto = an * 0.9f
        // Las plumas, que asoman inclinadas y dan su sombra en la pared.
        val boca = Offset(pie.x - an * 0.05f, pie.y - alto - an * 0.3f)
        val puntas = listOf(boca + Offset(-an * 0.9f, -an * 2.2f), boca + Offset(an * 0.7f, -an * 1.9f))
        puntas.forEach { q -> drawLine(a.sombra(if (a.oscuro) 0.18f else 0.14f), boca + caida * 2f, q + caida * 2.6f, 2.4f * u) }

        puntas.forEachIndexed { i, q ->
            val m = boca + (q - boca) * 0.35f
            val barba = Path().apply {
                moveTo(m.x, m.y)
                cubicTo(m.x - an * 0.35f, m.y - an * 0.3f, q.x - an * 0.25f, q.y + an * 0.25f, q.x, q.y)
                cubicTo(q.x + an * 0.12f, q.y + an * 0.4f, m.x + an * 0.2f, m.y - an * 0.1f, m.x, m.y)
                close()
            }
            drawPath(barba, a.luz(mezcla(VITELA, CALIZA_OSC, 0.15f + 0.2f * i), k))
            drawPath(borde(barba, -0.8f * u, 0f), a.sombra(0.25f))
            drawLine(a.luz(CALIZA_OSC, k), boca, q, 0.7f * u)
        }
        contacto(Offset(pie.x + 2f * u, pie.y), an * 1.7f, an * 0.16f, a)
        // El soporte: un taco con su agujero.
        val taco = Rect(pie.x - an * 0.7f, pie.y - alto * 0.55f, pie.x + an * 0.7f, pie.y)
        drawRect(a.luz(ROBLE, k), taco.topLeft, taco.size)
        drawRect(a.luz(ROBLE_LUZ, k), taco.topLeft, Size(taco.width, 1.6f * u))
        drawRect(a.sombra(0.3f), Offset(taco.right - an * 0.3f, taco.top), Size(an * 0.3f, taco.height))
        // El cuerno, metido en el taco, con su boca negra.
        val cuerno = Path().apply {
            moveTo(pie.x - an * 0.36f, pie.y - alto * 0.5f)
            cubicTo(pie.x - an * 0.4f, pie.y - alto * 0.8f, pie.x - an * 0.38f, pie.y - alto, pie.x - an * 0.32f, pie.y - alto - an * 0.3f)
            lineTo(pie.x + an * 0.26f, pie.y - alto - an * 0.3f)
            cubicTo(pie.x + an * 0.32f, pie.y - alto, pie.x + an * 0.34f, pie.y - alto * 0.8f, pie.x + an * 0.3f, pie.y - alto * 0.5f)
            close()
        }
        drawPath(cuerno, a.luz(mezcla(CUERNO, CUERO, 0.35f), k))
        drawPath(borde(cuerno, -an * 0.2f, 0f), a.sombra(0.3f))
        drawPath(borde(cuerno, an * 0.25f, 0f), a.claro(0.3f))
        drawOval(a.luz(TINTA, k), Offset(pie.x - an * 0.3f, pie.y - alto - an * 0.38f), Size(an * 0.56f, an * 0.16f))
        if (a.oscuro) drawLine(a.filo(0.6f * k), Offset(pie.x - an * 0.36f, pie.y - alto * 0.5f), Offset(pie.x - an * 0.32f, pie.y - alto - an * 0.3f), 1f * u)
        // El cortaplumas, tumbado delante.
        val c0 = Offset(pie.x + an * 0.8f, pie.y - 1.2f * u)
        drawLine(a.sombra(0.3f), c0 + Offset(1f * u, 1.4f * u), c0 + Offset(an * 1.1f, 1.4f * u), 2f * u)
        drawLine(a.luz(ROBLE_LUZ, k), c0, c0 + Offset(an * 0.55f, -0.4f * u), 2.2f * u, StrokeCap.Round)
        drawPath(poligono(c0.x + an * 0.55f, c0.y - 1.4f * u, c0.x + an * 1.1f, c0.y - 0.2f * u, c0.x + an * 0.55f, c0.y + 0.9f * u), a.luz(HIERRO_LUZ, k))
    }

    /**
     * El rollo de pergamino tumbado en la cornisa, con su cinta y, colgando por delante del canto de un cordón de cáñamo,
     * el sello de cera roja que lo cierra.
     */
    private fun DrawScope.rolloSellado(izq: Offset, largo: Float, ch: Float, a: Aire, k: Float, caida: Offset) {
        val u = density
        val r = largo * 0.11f
        val y = izq.y - r
        drawRoundRect(a.sombra(if (a.oscuro) 0.36f else 0.2f), Offset(izq.x + caida.x * 2f, y - r + caida.y * 2f), Size(largo, r * 2), CornerRadius(r))
        contacto(Offset(izq.x + largo * 0.5f, izq.y), largo * 1.05f, r * 0.5f, a)
        drawRoundRect(a.luz(VITELA, k), Offset(izq.x, y - r), Size(largo, r * 2), CornerRadius(r * 0.3f))
        drawRect(a.claro(0.35f), Offset(izq.x + 2f * u, y - r * 0.7f), Size(largo - 4f * u, r * 0.4f))
        drawRect(a.sombra(0.2f), Offset(izq.x, y + r * 0.3f), Size(largo, r * 0.7f))
        // La cabeza del rollo, con su espiral.
        drawOval(a.luz(mezcla(VITELA, CALIZA_OSC, 0.25f), k), Offset(izq.x + largo - r * 0.6f, y - r), Size(r * 1.2f, r * 2))
        drawArc(a.sombra(0.4f), 0f, 300f, false, Offset(izq.x + largo - r * 0.35f, y - r * 0.6f), Size(r * 0.7f, r * 1.2f), style = Stroke(0.6f * u))
        drawArc(a.sombra(0.4f), 40f, 280f, false, Offset(izq.x + largo - r * 0.15f, y - r * 0.25f), Size(r * 0.3f, r * 0.5f), style = Stroke(0.6f * u))
        // La hoja que se despega un poco, del lado de la luz.
        drawPath(poligono(izq.x + r * 0.2f, y + r, izq.x - r * 0.4f, y + r * 1.05f, izq.x + r * 0.4f, y + r * 0.4f), a.luz(VITELA, k))
        // La cinta y el cordón que baja por delante de la cornisa hasta el sello.
        val xc = izq.x + largo * 0.38f
        drawRect(a.luz(GRANA, k), Offset(xc - 2f * u, y - r), Size(4f * u, r * 2))
        val selloC = Offset(xc + largo * 0.06f, ch - largo * 0.08f)
        val cordon = Path().apply { moveTo(xc, y + r); cubicTo(xc + 1f * u, y + r + 8f * u, selloC.x - 4f * u, selloC.y - 10f * u, selloC.x, selloC.y - largo * 0.06f) }
        drawPath(cordon, a.luz(mezcla(CUERNO, CUERO, 0.4f), k), style = Stroke(1f * u))
        // El sello: una torta de cera roja, con su reborde y la impronta.
        val rs = largo * 0.085f
        drawCircle(a.sombra(0.45f), rs, selloC + Offset(1.6f * u, 2f * u))
        drawCircle(a.luz(GRANA, k), rs, selloC)
        drawCircle(a.luz(mezcla(GRANA, TINTA, 0.35f), k), rs * 0.68f, selloC)
        drawArc(a.claro(0.45f), 170f, 110f, false, Offset(selloC.x - rs * 0.9f, selloC.y - rs * 0.9f), Size(rs * 1.8f, rs * 1.8f), style = Stroke(0.8f * u))
        drawPath(estrellaSello(selloC, rs * 0.42f), a.luz(mezcla(GRANA, LUZ_MUEBLE, 0.25f), k))
        if (a.oscuro) drawArc(a.filo(0.7f * k), 160f, 100f, false, Offset(selloC.x - rs, selloC.y - rs), Size(rs * 2, rs * 2), style = Stroke(1f * u))
    }

    private fun estrellaSello(c: Offset, r: Float) = Path().apply {
        for (j in 0 until 12) {
            val ang = Math.toRadians(-90.0 + j * 30.0)
            val rr = if (j % 2 == 0) r else r * 0.45f
            val p = Offset(c.x + rr * cos(ang).toFloat(), c.y + rr * sin(ang).toFloat())
            if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }

    // ---------------------------------------------------------------- al pie

    /** El banco de coro bajo, cortado por el borde: el asiento con su moldura y el testero calado con un arco. */
    private fun DrawScope.banco(pie: Offset, an: Float, alto: Float, a: Aire, k: Float) {
        val u = density
        val asiento = Rect(pie.x - an * 0.3f, pie.y - alto + 3f * u, pie.x + an, pie.y - alto + 10f * u)
        contacto(Offset(pie.x + an * 0.5f, pie.y), an * 1.4f, 5f * u, a, 0.4f)
        drawRect(a.sombra(if (a.oscuro) 0.4f else 0.24f), Offset(asiento.left, asiento.bottom), Size(asiento.width, 5f * u))
        // El testero de la derecha.
        val tes = Rect(pie.x + an * 0.72f, asiento.bottom, pie.x + an * 0.88f, pie.y)
        drawRect(a.luz(ROBLE, k), tes.topLeft, tes.size)
        drawRect(a.claro(0.25f), tes.topLeft, Size(1.4f * u, tes.height))
        drawRect(a.sombra(0.3f), Offset(tes.right - 3f * u, tes.top), Size(3f * u, tes.height))
        val hueco = ojiva(Rect(tes.left + tes.width * 0.25f, tes.top + tes.height * 0.35f, tes.right - tes.width * 0.25f, tes.bottom), tes.width * 0.4f)
        drawPath(hueco, a.luz(mezcla(JUNTA, ROBLE_OSC, 0.5f), k * 0.6f))
        // El travesaño de abajo.
        drawRect(a.luz(ROBLE_OSC, k), Offset(asiento.left, pie.y - alto * 0.3f), Size(tes.left - asiento.left, 3f * u))
        // El asiento.
        drawRect(a.luz(mezcla(ROBLE, ROBLE_LUZ, 0.6f), k), Offset(asiento.left, asiento.top - 4f * u), Size(asiento.width, 4f * u))
        drawRect(a.luz(ROBLE, k), asiento.topLeft, asiento.size)
        vetas(asiento, ROBLE_OSC.copy(alpha = 0.4f), 911, densidad = 0.8f, grosor = 0.6f)
        drawRect(a.claro(0.4f), Offset(asiento.left, asiento.top - 4f * u), Size(asiento.width, 0.8f * u))

        drawRect(a.sombra(0.35f), Offset(asiento.left, asiento.bottom - 1.2f * u), Size(asiento.width, 1.2f * u))
        // Lo gastado del canto donde se sentaban.
        drawOval(a.claro(0.18f), Offset(asiento.left + an * 0.1f, asiento.top), Size(an * 0.4f, 2.4f * u))
    }

    /** El cesto de mimbre con unos rollos de pergamino de pie, uno con su sello. */
    private fun DrawScope.cesto(pie: Offset, an: Float, alto: Float, a: Aire, k: Float) {
        val u = density
        val cx = pie.x
        val boca = pie.y - alto
        contacto(Offset(cx + 3f * u, pie.y), an * 1.1f, 5f * u, a, 0.4f)
        // Los rollos, ladeados, de atrás adelante.
        listOf(Triple(-0.22f, -10f, 1.4f), Triple(0.18f, 12f, 1.2f), Triple(0f, -3f, 1.6f), Triple(0.3f, 22f, 0.9f)).forEachIndexed { i, (dx, ang, largo) ->
            val base = Offset(cx + an * dx, boca + alto * 0.4f)
            rotate(ang, base) {
                val rr = an * 0.075f
                val r = Rect(base.x - rr, base.y - alto * largo, base.x + rr, base.y)
                drawRect(a.luz(mezcla(VITELA, CUERNO, 0.15f * i), k), r.topLeft, r.size)
                drawRect(a.claro(0.3f), r.topLeft, Size(rr * 0.5f, r.height))
                drawRect(a.sombra(0.22f), Offset(r.right - rr * 0.6f, r.top), Size(rr * 0.6f, r.height))
                drawOval(a.luz(mezcla(VITELA, CALIZA_OSC, 0.3f), k), Offset(r.left, r.top - rr * 0.4f), Size(rr * 2, rr * 0.8f))
                drawArc(a.sombra(0.4f), 0f, 300f, false, Offset(r.left + rr * 0.5f, r.top - rr * 0.25f), Size(rr, rr * 0.5f), style = Stroke(0.6f * u))
                if (i == 2) drawRect(a.luz(GRANA, k), Offset(r.left, r.top + r.height * 0.3f), Size(r.width, 3f * u))
            }
        }
        val cuerpo = Path().apply {
            moveTo(cx - an * 0.5f, boca)
            cubicTo(cx - an * 0.52f, boca + alto * 0.5f, cx - an * 0.44f, pie.y - alto * 0.08f, cx - an * 0.38f, pie.y)
            lineTo(cx + an * 0.38f, pie.y)
            cubicTo(cx + an * 0.44f, pie.y - alto * 0.08f, cx + an * 0.52f, boca + alto * 0.5f, cx + an * 0.5f, boca)
            close()
        }
        drawPath(cuerpo, a.luz(CUERNO, k))
        clipPath(cuerpo) {
            for (j in 0 until 6) {
                val yy = boca + alto * (j + 0.5f) / 6
                drawLine(a.sombra(0.3f), Offset(cx - an, yy + 2f * u), Offset(cx + an, yy + 2f * u), 0.8f * u)
                var x = cx - an * 0.56f + (j % 2) * 2.4f * u
                while (x < cx + an * 0.56f) { drawLine(a.luz(mezcla(CUERNO, CUERO, 0.55f), k), Offset(x, yy - 1.6f * u), Offset(x + 0.4f * u, yy + 2f * u), 1.1f * u); x += maxOf(1f, 4.8f * u) }
            }
            drawRect(a.claro(0.2f), Offset(cx - an * 0.5f, boca), Size(an * 0.18f, alto))
            drawRect(a.sombra(0.3f), Offset(cx + an * 0.2f, boca), Size(an * 0.32f, alto))
        }
        drawRoundRect(a.luz(mezcla(CUERNO, CUERO, 0.3f), k), Offset(cx - an * 0.53f, boca - 2.4f * u), Size(an * 1.06f, 4.8f * u), CornerRadius(2.4f * u))
        drawRect(if (a.oscuro) a.filo(0.4f * k) else a.claro(0.4f), Offset(cx - an * 0.5f, boca - 2.4f * u), Size(an, 1f * u))
    }

    // ---------------------------------------------------------------- los huecos libres de las repisas

    /** Lo que hay donde una repisa queda libre: elegido por el ancho del hueco y por la repisa, para que no se repita. */
    private fun DrawScope.huecoLibre(k: Int, r: Rect, oc: Float, a: Aire, cerca: Float) {
        val u = density
        val libre = (r.right - oc) / u
        val y = r.bottom
        when {
            libre >= 150f -> if (k % 2 == 1) {
                saquito(Offset(oc + 1f * u, y), 26f * u, a, cerca + 0.03f, apoyado = true)
                cajaPomez(Offset(oc + 44f * u, y), 46f * u, a, cerca)
                if (libre >= 220f) palmatoria(Offset(oc + 120f * u, y), 22f * u, a, cerca)
                libroTumbado(Offset(r.right - 104f * u, y), 92f * u, r.top, a, cerca - 0.03f, k)
            } else {
                libroTumbado(Offset(oc + 6f * u, y), 92f * u, r.top, a, cerca + 0.02f, k)
                bifolios(Offset(r.right - 66f * u, y), 52f * u, a, cerca - 0.03f, k)
            }
            libre >= 92f -> when (k % 3) {
                0 -> libroTumbado(Offset(oc + 5f * u, y), minOf(80f * u, libre * u - 12f * u), r.top, a, cerca, k)
                1 -> { saquito(Offset(oc + 1f * u, y), 24f * u, a, cerca, apoyado = true); bifolios(Offset(oc + 34f * u, y), 46f * u, a, cerca, k) }
                else -> { cajaPomez(Offset(oc + 8f * u, y), 44f * u, a, cerca); if (libre >= 110f) palmatoria(Offset(oc + 78f * u, y), 22f * u, a, cerca) }
            }
            libre >= 50f -> when (k % 3) {
                0 -> saquito(Offset(oc + 1f * u, y), 24f * u, a, cerca, apoyado = true)
                1 -> palmatoria(Offset((oc + r.right) / 2, y), 22f * u, a, cerca)
                else -> bifolios(Offset(oc + 3f * u, y), minOf(44f * u, libre * u - 6f * u), a, cerca, k)
            }
            libre >= 28f -> saquito(Offset(oc + 1f * u, y), 20f * u, a, cerca, apoyado = true)
        }
    }

    /**
     * El libro grande de coro tumbado, visto de canto: las tablas con sus bullones (que asoman por arriba), el canto dorado
     * de las hojas, el broche, y la cadena que lo ata a una argolla clavada bajo la tabla de arriba.
     */
    private fun DrawScope.libroTumbado(izq: Offset, largo: Float, techo: Float, a: Aire, k: Float, semilla: Int) {
        val u = density
        val g = largo * 0.24f
        val y0 = izq.y - g
        contacto(Offset(izq.x + largo * 0.5f, izq.y), largo * 1.05f, 5f * u, a, 0.4f)
        drawRect(a.sombra(if (a.oscuro) 0.36f else 0.22f), Offset(izq.x + 10f * u, y0 - 4f * u), Size(largo, g))
        // Los bullones de la tapa de arriba, que asoman.
        for (f in listOf(0.12f, 0.5f, 0.88f)) {
            val c = Offset(izq.x + largo * f, y0 - 0.5f * u)
            drawArc(a.luz(LATON, k), 180f, 180f, true, Offset(c.x - 3.6f * u, c.y - 3f * u), Size(7.2f * u, 6f * u))
            drawCircle(a.claro(0.5f), 0.9f * u, c - Offset(1f * u, 1.3f * u))
        }
        val tela = a.luz(TERCIOPELOS[Math.floorMod(semilla, TERCIOPELOS.size)], k)
        // Las dos tablas y, entre ellas, el canto de las hojas dorado.
        val tapa = g * 0.2f
        drawRect(tela, Offset(izq.x, y0), Size(largo, tapa))
        drawRect(tela, Offset(izq.x, izq.y - tapa), Size(largo, tapa))
        drawRect(a.luz(mezcla(ORO, CUERO, 0.2f), k), Offset(izq.x + 2f * u, y0 + tapa), Size(largo - 4f * u, g - 2 * tapa))
        for (j in 1 until 7) drawLine(a.luz(mezcla(ORO, CUERO, 0.6f), k).copy(alpha = 0.6f), Offset(izq.x + 2f * u, y0 + tapa + (g - 2 * tapa) * j / 7f), Offset(izq.x + largo - 2f * u, y0 + tapa + (g - 2 * tapa) * j / 7f), 0.5f * u)
        drawRect(a.claro(0.3f), Offset(izq.x + 2f * u, y0 + tapa), Size(largo * 0.5f, 1.2f * u))
        drawRect(a.claro(0.3f), Offset(izq.x, y0), Size(largo, 1f * u))
        drawRect(a.sombra(0.3f), Offset(izq.x + largo - largo * 0.14f, y0), Size(largo * 0.14f, g))
        // El broche que cierra el canto.
        drawRect(a.luz(LATON, k), Offset(izq.x + largo * 0.45f, y0 + 1f * u), Size(5f * u, g - 2f * u))
        drawCircle(a.luz(ORO, k), 1.4f * u, Offset(izq.x + largo * 0.45f + 2.5f * u, y0 + g / 2))
        if (a.oscuro) drawRect(a.filo(0.6f * k), Offset(izq.x, y0), Size(largo * 0.6f, 1f * u))
        // La cadena: de la argolla del canto derecho a la argolla bajo la tabla de arriba.
        val ancla = Offset(izq.x + largo - 3f * u, y0 + g * 0.5f)
        val arriba = Offset(izq.x + largo + 10f * u, techo + 3f * u)
        drawRect(a.luz(HIERRO, k), Offset(arriba.x - 4f * u, techo), Size(8f * u, 2.4f * u))
        cadena(arriba, ancla, Offset((arriba.x + ancla.x) / 2 + 12f * u, (arriba.y + ancla.y) / 2 + 26f * u), a, k)
        drawCircle(a.luz(HIERRO_LUZ, k), 2.2f * u, ancla, style = Stroke(1.1f * u))
    }

    /** Una cadena de eslabones de [desde] a [hasta] que cuelga pasando cerca de [panza]. */
    private fun DrawScope.cadena(desde: Offset, hasta: Offset, panza: Offset, a: Aire, k: Float) {
        val u = density
        val pasos = 18
        var prev = desde
        for (i in 1..pasos) {
            val t = i / pasos.toFloat(); val m = 1f - t
            val p = desde * (m * m) + panza * (2 * m * t) + hasta * (t * t)
            val c = (prev + p) / 2f
            val d = p - prev
            val ang = Math.toDegrees(kotlin.math.atan2(d.y.toDouble(), d.x.toDouble())).toFloat()
            val l = d.getDistance().coerceAtLeast(1f)
            rotate(ang, c) {
                if (i % 2 == 0) {
                    drawOval(a.sombra(0.35f), Offset(c.x - l * 0.6f + 1.2f * u, c.y - 1.2f * u + 1.6f * u), Size(l * 1.2f, 2.4f * u), style = Stroke(0.8f * u))
                    drawOval(a.luz(HIERRO_LUZ, k), Offset(c.x - l * 0.6f, c.y - 1.2f * u), Size(l * 1.2f, 2.4f * u), style = Stroke(0.9f * u))
                } else drawLine(a.luz(HIERRO, k), Offset(c.x - l * 0.55f, c.y), Offset(c.x + l * 0.55f, c.y), 1.2f * u, StrokeCap.Round)
            }
            prev = p
        }
    }

    /** La caja de madera abierta con la piedra pómez para el pergamino y el punzón de pautar cruzado encima. */
    private fun DrawScope.cajaPomez(izq: Offset, an: Float, a: Aire, k: Float) {
        val u = density
        val alto = an * 0.36f
        contacto(Offset(izq.x + an * 0.5f, izq.y), an * 1.15f, 5f * u, a, 0.4f)
        // La tapa abierta, apoyada atrás.
        val tapa = poligono(izq.x + 2f * u, izq.y - alto, izq.x + an * 0.12f, izq.y - alto - an * 0.5f, izq.x + an * 1.0f, izq.y - alto - an * 0.5f, izq.x + an - 2f * u, izq.y - alto)
        drawPath(corrido(tapa, 6f * u, 3f * u), a.sombra(if (a.oscuro) 0.34f else 0.2f))
        drawPath(tapa, a.luz(mezcla(ROBLE, ROBLE_LUZ, 0.3f), k))
        drawPath(borde(tapa, 2f * u, 2f * u), a.claro(0.2f))
        // El interior y la pómez, gris y porosa, que asoma.
        drawRect(a.luz(ROBLE_OSC, k), Offset(izq.x + 2f * u, izq.y - alto - 2f * u), Size(an - 4f * u, 4f * u))
        val pomez = Offset(izq.x + an * 0.38f, izq.y - alto - 2f * u)
        drawOval(a.luz(mezcla(CALIZA, LUNA, 0.3f), k), Offset(pomez.x - an * 0.16f, pomez.y - an * 0.1f), Size(an * 0.32f, an * 0.18f))
        for (j in 0 until 5) drawCircle(a.sombra(0.35f), 0.7f * u, Offset(pomez.x + an * (azar(931, j) - 0.5f) * 0.24f, pomez.y + an * (azar(932, j) - 0.5f) * 0.1f))
        // La caja.
        drawRect(a.luz(ROBLE, k), Offset(izq.x, izq.y - alto), Size(an, alto))
        vetas(Rect(izq.x, izq.y - alto, izq.x + an, izq.y), ROBLE_OSC.copy(alpha = 0.4f), 933, densidad = 0.8f, grosor = 0.6f, ondulacion = 0.3f)

        drawRect(a.claro(0.35f), Offset(izq.x, izq.y - alto), Size(an, 1f * u))
        drawRect(a.sombra(0.3f), Offset(izq.x + an * 0.82f, izq.y - alto), Size(an * 0.18f, alto))
        // El punzón, cruzado.
        drawLine(a.luz(ROBLE_LUZ, k), Offset(izq.x + an * 0.55f, izq.y - alto - 1f * u), Offset(izq.x + an * 0.85f, izq.y - alto - 6f * u), 2.4f * u, StrokeCap.Round)
        drawLine(a.luz(HIERRO_LUZ, k), Offset(izq.x + an * 0.85f, izq.y - alto - 6f * u), Offset(izq.x + an * 1.08f, izq.y - alto - 9.5f * u), 1f * u)
        if (a.oscuro) drawRect(a.filo(0.5f * k), Offset(izq.x, izq.y - alto), Size(an * 0.7f, 0.9f * u))
    }

    /** Una pila de bifolios de pergamino sin encuadernar, de bordes desiguales, con uno que se curva. */
    private fun DrawScope.bifolios(izq: Offset, an: Float, a: Aire, k: Float, semilla: Int) {
        val u = density
        val hojas = 9
        val g = 1.6f * u
        contacto(Offset(izq.x + an * 0.5f, izq.y), an * 1.15f, 4f * u, a, 0.4f)
        drawRect(a.sombra(if (a.oscuro) 0.34f else 0.2f), Offset(izq.x + 8f * u, izq.y - hojas * g + 2f * u), Size(an, hojas * g - 2f * u))
        for (j in 0 until hojas) {
            val dx = (azar(semilla + 940, j) - 0.5f) * 5f * u
            val r = Rect(izq.x + dx, izq.y - (j + 1) * g, izq.x + dx + an * (0.94f + 0.06f * azar(semilla + 941, j)), izq.y - j * g)
            drawRect(a.luz(mezcla(VITELA, CUERNO, 0.25f * azar(semilla + 942, j)), k), r.topLeft, r.size)
            drawLine(a.sombra(0.25f), Offset(r.left, r.bottom), Offset(r.right, r.bottom), 0.4f * u)
        }
        val top = izq.y - hojas * g
        drawRect(a.claro(0.4f), Offset(izq.x, top), Size(an, 0.8f * u))
        // La hoja de arriba, que se curva por la esquina.
        drawPath(Path().apply { moveTo(izq.x + an * 0.7f, top); cubicTo(izq.x + an * 0.85f, top - 4f * u, izq.x + an, top - 3f * u, izq.x + an * 1.02f, top - 6f * u); lineTo(izq.x + an, top); close() }, a.luz(VITELA, k + 0.05f))
        // Unas líneas de pauta en la de arriba, como letra (no se lee).
        for (j in 0 until 3) drawLine(a.luz(TINTA, k).copy(alpha = 0.3f), Offset(izq.x + an * 0.1f, top - 0.2f * u), Offset(izq.x + an * (0.3f + 0.15f * j), top - 0.2f * u), 0.4f * u)
        drawRect(a.sombra(0.2f), Offset(izq.x + an * 0.84f, top), Size(an * 0.16f, hojas * g))
        if (a.oscuro) drawRect(a.filo(0.5f * k), Offset(izq.x, top), Size(an * 0.7f, 0.8f * u))
    }

    /** La palmatoria apagada: el platillo de latón con su asa de anilla y el cabo de vela con la cera chorreada. */
    private fun DrawScope.palmatoria(pie: Offset, an: Float, a: Aire, k: Float) {
        val u = density
        contacto(Offset(pie.x + 2f * u, pie.y), an * 1.2f, 4f * u, a, 0.4f)
        val vela = Rect(pie.x - an * 0.14f, pie.y - an * 1.0f, pie.x + an * 0.14f, pie.y - an * 0.2f)
        drawRect(a.sombra(if (a.oscuro) 0.34f else 0.2f), vela.topLeft + Offset(5f * u, 2f * u), vela.size)
        drawRect(a.luz(SEBO, k), vela.topLeft, vela.size)
        drawRect(a.claro(0.3f), vela.topLeft, Size(vela.width * 0.3f, vela.height))
        drawRect(a.sombra(0.2f), Offset(vela.right - vela.width * 0.3f, vela.top), Size(vela.width * 0.3f, vela.height))
        drawPath(poligono(vela.left, vela.top + 1f * u, vela.left - 1.2f * u, vela.top + an * 0.35f, vela.left + 1f * u, vela.top + an * 0.3f), a.luz(SEBO, k))
        drawLine(a.luz(TINTA, k), Offset(vela.center.x, vela.top), Offset(vela.center.x + 1f * u, vela.top - 3f * u), 0.8f * u)
        // La arandela, el platillo y el asa.
        drawRect(a.luz(LATON, k), Offset(pie.x - an * 0.2f, vela.bottom - 1f * u), Size(an * 0.4f, 3f * u))
        drawArc(a.luz(LATON, k), 90f, 240f, false, Offset(pie.x + an * 0.38f, pie.y - an * 0.4f), Size(an * 0.32f, an * 0.32f), style = Stroke(1.4f * u))
        drawOval(a.luz(LATON, k), Offset(pie.x - an * 0.5f, pie.y - an * 0.24f), Size(an, an * 0.24f))
        drawArc(a.claro(0.5f), 180f, 100f, false, Offset(pie.x - an * 0.5f, pie.y - an * 0.24f), Size(an, an * 0.24f), style = Stroke(0.8f * u))
        drawRect(a.sombra(0.3f), Offset(pie.x + an * 0.1f, pie.y - an * 0.12f), Size(an * 0.38f, an * 0.1f))
        if (a.oscuro) drawArc(a.filo(0.6f * k), 180f, 90f, false, Offset(pie.x - an * 0.5f, pie.y - an * 0.24f), Size(an, an * 0.24f), style = Stroke(1f * u))
    }

    /** El saquito de cuero atado con su cordón; con [apoyado], recostado en el último volumen, de sujetalibros. */
    private fun DrawScope.saquito(izq: Offset, an: Float, a: Aire, k: Float, apoyado: Boolean) {
        val u = density
        val alto = an * 1.1f
        val x = izq.x
        val cuerpo = Path().apply {
            moveTo(x + an * 0.08f, izq.y)
            cubicTo(x - an * 0.04f, izq.y - alto * 0.5f, x + an * 0.1f, izq.y - alto * 0.72f, x + an * 0.3f, izq.y - alto * 0.78f)
            lineTo(x + an * (if (apoyado) 0.1f else 0.35f), izq.y - alto)
            lineTo(x + an * (if (apoyado) 0.38f else 0.55f), izq.y - alto * 0.86f)
            lineTo(x + an * 0.68f, izq.y - alto * 0.98f)
            lineTo(x + an * 0.62f, izq.y - alto * 0.76f)
            cubicTo(x + an * 0.95f, izq.y - alto * 0.66f, x + an * 1.05f, izq.y - alto * 0.3f, x + an * 0.9f, izq.y)
            close()
        }
        drawPath(corrido(cuerpo, 6f * u, 2f * u), a.sombra(if (a.oscuro) 0.36f else 0.22f))
        contacto(Offset(x + an * 0.5f, izq.y), an * 1.2f, 4f * u, a, 0.4f)
        drawPath(cuerpo, a.luz(CUERO, k))
        clipPath(cuerpo) {
            drawRect(a.claro(0.2f), Offset(x, izq.y - alto), Size(an * 0.28f, alto))
            drawRect(a.sombra(0.3f), Offset(x + an * 0.66f, izq.y - alto), Size(an * 0.4f, alto))
            drawLine(a.sombra(0.35f), Offset(x + an * 0.4f, izq.y - alto * 0.7f), Offset(x + an * 0.32f, izq.y - alto * 0.15f), 0.8f * u)
            drawLine(a.sombra(0.3f), Offset(x + an * 0.6f, izq.y - alto * 0.68f), Offset(x + an * 0.7f, izq.y - alto * 0.3f), 0.7f * u)
        }
        // El cordón del fruncido, con su lazada.
        drawLine(a.luz(mezcla(CUERNO, CUERO, 0.4f), k), Offset(x + an * 0.22f, izq.y - alto * 0.76f), Offset(x + an * 0.66f, izq.y - alto * 0.74f), 1.4f * u)
        drawLine(a.luz(mezcla(CUERNO, CUERO, 0.4f), k), Offset(x + an * 0.6f, izq.y - alto * 0.75f), Offset(x + an * 0.82f, izq.y - alto * 0.5f), 1f * u, StrokeCap.Round)
        if (a.oscuro) drawPath(borde(cuerpo, 1.2f * u, 0.8f * u), a.filo(0.4f * k))
    }

    // ---------------------------------------------------------------- cada entrada

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = Aire(oscuro, v.noche)
        val k = if (a.lumbre) cercaCuerpo(v.repisa + 0.55f, v.repisas, LADO + v.centro * (1f - 2 * LADO)) else 1f
        when (clase(v.semilla)) {
            0 -> horas(v, medidor, a, k)
            1 -> codice(v, medidor, a, k)
            else -> atado(v, medidor, a, k)

        }
    }

    /** Un bullón (o, en los Sueños, la luna de plata en su lugar). */
    private fun DrawScope.bullon(c: Offset, r: Float, luna: Boolean, fondo: Color, a: Aire, k: Float) {
        if (luna) {
            drawCircle(if (a.oscuro) mezcla(LUNA, a.hondo, 0.25f - 0.25f * k) else LUNA, r, c)
            drawCircle(fondo, r * 0.82f, Offset(c.x + r * 0.42f, c.y - r * 0.26f))
        } else {
            drawCircle(a.sombra(0.4f), r, c + Offset(r * 0.25f, r * 0.3f))
            drawCircle(a.luz(ORO, k), r, c)
            drawCircle(a.luz(mezcla(ORO, CUERO, 0.4f), k), r * 0.5f, c + Offset(r * 0.2f, r * 0.2f))
            drawCircle(if (a.oscuro) a.filo(0.9f * k) else a.claro(0.6f), r * 0.32f, c - Offset(r * 0.35f, r * 0.35f))
        }
    }

    /**
     * El libro de horas de terciopelo (el de la tapa): el terciopelo es el color del ánimo, con su pelo (luz a la izquierda,
     * sombra a la derecha) y su roce; bullones de latón en las esquinas que asoman del perfil, el bullón del medio (en los
     * Sueños, la luna de plata), dos broches que sobresalen por la derecha, la cartela de latón con el día en vitela, el mes
     * en oro y, abajo, los registros de seda (más cuanto más escribiste).
     */
    private fun DrawScope.horas(v: Volumen, medidor: TextMeasurer, a: Aire, k: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val tinte = TERCIOPELOS[s % TERCIOPELOS.size]
        val tela = v.tono?.let { a.animo(mezcla(it, PELO, if (a.oscuro) 0.18f else 0.4f), k) } ?: a.luz(tinte, k)
        // Los registros, que salen de entre las hojas por abajo.
        val regs = 1 + (v.lleno * 3f).toInt()
        for (j in 0 until regs) {
            val x = w * (0.26f + 0.14f * j)
            drawPath(poligono(x, h * 0.9f, x + 2.6f * u, h * 0.9f, x + 2.6f * u + j * 0.5f * u, h, x + 1.3f * u, h - 1.8f * u, x + j * 0.5f * u, h), a.luz(REGISTROS[(s + j) % REGISTROS.size], k))
        }
        val cuerpo = Rect(1.6f * u, 1.6f * u, w - 2.4f * u, h * 0.94f)
        drawRoundRect(tela, cuerpo.topLeft, cuerpo.size, CornerRadius(2.4f * u))
        // El pelo del terciopelo y el roce de las manos.
        drawRect(a.claro(0.2f), Offset(cuerpo.left + 1f * u, cuerpo.top + 2f * u), Size(w * 0.11f, cuerpo.height - 4f * u))
        drawRect(a.sombra(0.2f), Offset(cuerpo.right - w * 0.2f, cuerpo.top), Size(w * 0.2f, cuerpo.height))
        drawRect(a.sombra(0.2f), Offset(cuerpo.right - w * 0.08f, cuerpo.top), Size(w * 0.08f, cuerpo.height))
        drawOval(a.claro(0.1f), Offset(w * 0.2f, h * (0.56f + 0.2f * azar(s, 4))), Size(w * 0.5f, h * 0.1f))
        // El filete dorado de la tapa.
        drawRoundRect(a.luz(ORO, k).copy(alpha = 0.55f), Offset(cuerpo.left + 3f * u, cuerpo.top + 3f * u), Size(cuerpo.width - 6f * u, cuerpo.height - 6f * u), CornerRadius(1.5f * u), style = Stroke(0.6f * u))
        // El canto de las hojas, abajo.
        drawRect(a.luz(mezcla(VITELA, ORO, 0.4f), k), Offset(cuerpo.left + 2f * u, cuerpo.bottom - 1.6f * u), Size(cuerpo.width - 4f * u, 1.6f * u))
        // Los broches, que sobresalen por la derecha.
        for (f in listOf(0.25f, 0.78f)) {
            val y = h * f
            drawRect(a.sombra(0.35f), Offset(w * 0.74f, y - 1.2f * u), Size(w * 0.26f, 3.6f * u))
            drawRect(a.luz(LATON, k), Offset(w * 0.74f, y - 2f * u), Size(w * 0.26f - 0.3f * u, 3.6f * u))
            drawRect(a.claro(0.4f), Offset(w * 0.74f, y - 2f * u), Size(w * 0.26f, 0.7f * u))
            drawCircle(a.luz(ORO, k), 1.1f * u, Offset(w * 0.82f, y - 0.2f * u))
        }
        // Los bullones de las esquinas, que asoman del perfil.
        val rb = minOf(w * 0.13f, 3.5f * u)
        for (o in listOf(Offset(cuerpo.left + rb * 0.7f, cuerpo.top + rb * 0.7f), Offset(cuerpo.right - rb * 0.7f, cuerpo.top + rb * 0.7f), Offset(cuerpo.left + rb * 0.7f, cuerpo.bottom - rb * 0.9f), Offset(cuerpo.right - rb * 0.7f, cuerpo.bottom - rb * 0.9f)))
            bullon(o, rb, false, tela, a, k)
        bullon(Offset(w * 0.47f, h * 0.15f), minOf(w * 0.18f, 4.8f * u), v.noche, tela, a, k)
        // La cartela de latón con el día en vitela.
        val cart = Rect(w * 0.1f, h * 0.24f, w * 0.86f, h * 0.24f + 21f * u)
        drawRect(a.sombra(0.45f), cart.topLeft + Offset(0.9f * u, 1.1f * u), cart.size)
        drawRect(a.luz(LATON, maxOf(k, 0.2f)), cart.topLeft, cart.size)
        val vit = Rect(cart.left + 1.6f * u, cart.top + 1.6f * u, cart.right - 1.6f * u, cart.bottom - 1.6f * u)
        drawRect(a.etiqueta(k), vit.topLeft, vit.size)
        drawRect(a.claro(0.5f), cart.topLeft, Size(cart.width, 0.7f * u))
        textoEn(medidor, v.dia, Rect(vit.left, vit.top + 0.4f * u, vit.right, vit.bottom), LETRA, a.tinta, 14.5f, peso = FontWeight.Bold)
        // El mes, en oro a lo largo.
        val mes = Rect(w * 0.18f, cart.bottom + 3f * u, w * 0.7f, h * 0.76f)
        if (mes.height > 10f * u) textoEn(medidor, v.mes, mes, LETRA, if (a.oscuro) mezcla(ORO, a.hondo, 0.35f - 0.3f * k) else mezcla(ORO, LUZ_MUEBLE, 0.2f), 9f, vertical = true, minimo = 5f)
        // El filo del farol en la cabeza y el costado que lo miran.
        if (a.oscuro && k > 0.08f) {
            drawRect(a.filo(0.65f * k), Offset(cuerpo.left + 2f * u, cuerpo.top), Size(cuerpo.width - 4f * u, 1f * u))
            drawRect(a.filo(0.4f * k), Offset(cuerpo.left, cuerpo.top + 2f * u), Size(1f * u, cuerpo.height - 4f * u))
        }
    }

    /**
     * El códice de tablas forradas de cuero claro (blanco de cerdo o pardo), visto por el lomo: el más alto. Los nervios
     * sobresalen del perfil; arriba, la cofia y los registros de seda (más cuanto más escribiste); el tejuelo de pergamino
     * clavado con el día, un bullón de latón (en los Sueños, la luna), la cinta de seda ancha del color del ánimo que lo
     * ciñe, el mes a pluma y la correa con su cierre.
     */
    private fun DrawScope.codice(v: Volumen, medidor: TextMeasurer, a: Aire, k: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val top = 5f * u
        val cuero = a.luz(if (s % 3 == 0) mezcla(CUERO_BLANCO, CUERO, 0.55f) else mezcla(CUERO_BLANCO, CUERNO, 0.15f * azar(s, 3)), k)
        // Los registros que salen por la cabeza.
        val regs = 1 + (v.lleno * 3f).toInt()
        for (j in 0 until regs) {
            val x = w * (0.3f + 0.15f * j)
            drawLine(a.luz(REGISTROS[(s + j + 1) % REGISTROS.size], k), Offset(x, top + 1f * u), Offset(x + (j - 1) * 1.2f * u, 0.4f * u), 1.6f * u)
        }
        val cuerpo = Rect(1.2f * u, top, w - 1.2f * u, h)
        drawRect(cuero, cuerpo.topLeft, cuerpo.size)
        // La cofia: el cuero que remata el lomo arriba.
        drawRoundRect(cuero, Offset(w * 0.25f, top - 2.2f * u), Size(w * 0.5f, 4f * u), CornerRadius(2f * u))
        // El lomo redondo: luz a la izquierda, sombra a la derecha, y el roce.
        drawRect(a.claro(0.26f), Offset(cuerpo.left + 1.5f * u, top), Size(w * 0.12f, h - top))
        drawRect(a.sombra(0.16f), Offset(w * 0.72f, top), Size(w * 0.28f, h - top))
        drawRect(a.sombra(0.16f), Offset(w * 0.88f, top), Size(w * 0.12f, h - top))
        drawOval(a.sombra(0.1f), Offset(w * 0.15f, h * (0.3f + 0.3f * azar(s, 5))), Size(w * 0.5f, h * 0.12f))
        // Los nervios: bandas que sobresalen del perfil.
        val nervios = listOf(0.12f, 0.46f, 0.66f, 0.9f)
        for (f in nervios) {
            val y = top + (h - top) * f
            drawRect(a.sombra(0.3f), Offset(0f, y + 2.4f * u), Size(w, 1f * u))
            drawRect(mezcla(cuero, a.hondo, 0.12f), Offset(0f, y - 1.6f * u), Size(w, 4f * u))
            drawRect(a.claro(0.4f), Offset(0f, y - 1.6f * u), Size(w * 0.7f, 0.8f * u))
            drawRect(a.sombra(0.22f), Offset(w * 0.82f, y - 1.6f * u), Size(w * 0.18f, 4f * u))
        }
        // El bullón (o la luna) entre la cabeza y el primer nervio.
        bullon(Offset(w * 0.5f, top + (h - top) * 0.06f), minOf(w * 0.14f, 3.6f * u), v.noche, cuero, a, k)
        // El tejuelo de pergamino clavado con el día.
        val et = Rect(w * 0.1f, top + (h - top) * 0.12f + 4f * u, w * 0.9f, top + (h - top) * 0.12f + 24f * u)
        drawRect(a.sombra(0.4f), et.topLeft + Offset(0.8f * u, 1f * u), et.size)
        drawRect(a.etiqueta(k), et.topLeft, et.size)
        drawRect(a.sombra(0.12f), Offset(et.right - et.width * 0.15f, et.top), Size(et.width * 0.15f, et.height))
        for (p in listOf(Offset(et.left + 1.6f * u, et.top + 1.6f * u), Offset(et.right - 1.6f * u, et.top + 1.6f * u), Offset(et.left + 1.6f * u, et.bottom - 1.6f * u), Offset(et.right - 1.6f * u, et.bottom - 1.6f * u)))
            drawCircle(a.luz(HIERRO_LUZ, maxOf(k, 0.3f)), 0.75f * u, p)
        textoEn(medidor, v.dia, Rect(et.left + 2f * u, et.top + 0.5f * u, et.right - 2f * u, et.bottom), LETRA, a.tinta, 14.5f, peso = FontWeight.Bold)
        // La cinta de seda ancha del color del ánimo (cruda si no se marcó), con su cabo.
        val yc = top + (h - top) * 0.5f
        val seda = v.tono?.let { a.animo(mezcla(it, PELO, if (a.oscuro) 0.05f else 0.15f), k) } ?: a.luz(mezcla(VITELA, CUERNO, 0.3f), k)
        val gc = (h - top) * 0.13f
        drawRect(a.sombra(0.35f), Offset(0f, yc + gc), Size(w, 1.2f * u))
        drawRect(seda, Offset(0f, yc), Size(w, gc))
        drawRect(a.claro(0.3f), Offset(0f, yc), Size(w * 0.6f, 0.8f * u))
        drawRect(mezcla(seda, a.hondo, 0.3f), Offset(w * 0.8f, yc), Size(w * 0.2f, gc))
        drawPath(poligono(w * 0.18f, yc + gc, w * 0.34f, yc + gc, w * 0.36f, yc + gc + 7f * u, w * 0.26f, yc + gc + 5f * u, w * 0.16f, yc + gc + 7.5f * u), seda)
        // El mes, a pluma, entre la cinta y el nervio de abajo.
        val mes = Rect(w * 0.24f, yc + gc + 8f * u, w * 0.76f, top + (h - top) * 0.9f - 3f * u)
        if (mes.height > 10f * u) textoEn(medidor, v.mes, mes, LETRA, if (cuero.red + cuero.green > 0.9f) TINTA.copy(alpha = 0.8f) else a.etiqueta(k), 8.5f, vertical = true, minimo = 4.5f)
        // La correa con su cierre, sobre el nervio de abajo.
        val yr = top + (h - top) * 0.79f
        drawRect(a.sombra(0.35f), Offset(0f, yr + 3.6f * u), Size(w, 1f * u))
        drawRect(a.luz(CUERO, k), Offset(0f, yr), Size(w, 3.6f * u))
        drawRect(a.luz(LATON, k), Offset(w * 0.4f, yr - 1f * u), Size(w * 0.22f, 5.6f * u))
        drawRect(a.claro(0.45f), Offset(w * 0.4f, yr - 1f * u), Size(w * 0.22f, 0.8f * u))
        if (a.oscuro && k > 0.08f) {
            drawRect(a.filo(0.6f * k), Offset(cuerpo.left, top), Size(cuerpo.width, 1f * u))
            drawRect(a.filo(0.4f * k), Offset(cuerpo.left, top), Size(1f * u, h - top))
        }
    }

    /**
     * El atado de cuadernillos sin encuadernar (los pliegos que se copiaban por piezas): de cuatro a ocho cuadernillos de
     * pergamino de pie, el más bajo y algo panzudo, con el lomo de pliegues a la vista (cada doblez una costilla redondeada
     * con sus puntadas) y los cantos desparejos por arriba; abajo, la camisa de pergamino más oscuro que no llega arriba, y
     * todo atado en cruz con una correa ancha del color del ánimo, con su hebilla al frente (en los Sueños, un sello de cera
     * con la luna sobre el nudo). La papeleta con el día va cosida a la camisa. Cuanto más escribiste, más cuadernillos y más
     * hojas asomando.
     */
    private fun DrawScope.atado(v: Volumen, medidor: TextMeasurer, a: Aire, k: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val n = 4 + (v.lleno * 4f).toInt()
        // El perfil panzudo: los costados se abomban un poco hacia el medio.
        val panza = Path().apply {
            moveTo(1.6f * u, 0f); lineTo(w - 1.6f * u, 0f)
            cubicTo(w - 0.2f * u, h * 0.3f, w, h * 0.7f, w - 0.8f * u, h)
            lineTo(0.8f * u, h)
            cubicTo(0f, h * 0.7f, 0.2f * u, h * 0.3f, 1.6f * u, 0f)
            close()
        }
        val paso = (w - 2f * u) / n
        val yCamisa = h * 0.5f
        clipPath(panza) {
            // Las costillas: el doblez de cada cuadernillo, con la cabeza a distinta altura.
            val sombras = Path(); val luces = Path(); val puntadas = Path(); val asoman = Path()
            for (j in 0 until n) {
                val x0 = 1f * u + paso * j
                val top = (1.6f + 4.2f * azar(s, 30 + j)) * u
                drawRoundRect(a.luz(mezcla(VITELA, CUERNO, 0.08f + 0.26f * azar(s, 40 + j)), k), Offset(x0, top), Size(paso, h - top), CornerRadius(minOf(paso * 0.2f, 1.4f * u)))
                sombras.addRect(Rect(x0 + paso * 0.7f, top, x0 + paso, h))
                luces.addRect(Rect(x0 + paso * 0.12f, top + 2.6f * u, x0 + paso * 0.3f, yCamisa))
                // La cabeza: las hojas en capas, una raya bajo el canto de arriba.
                sombras.addRect(Rect(x0, top + 1.4f * u, x0 + paso, top + 2f * u))
                // Las puntadas del hilo, de través en el doblez.
                for (f in listOf(0.34f, 0.7f)) {
                    val y = top + (yCamisa - top) * f
                    puntadas.addRect(Rect(x0 + paso * 0.22f, y, x0 + paso * 0.78f, y + 0.9f * u))
                }
                // Las hojas que asoman por arriba, más cuanto más escribiste.
                if (azar(s, 50 + j) < 0.35f + 0.6f * v.lleno) {
                    val dx = paso * 0.3f * (azar(s, 60 + j) - 0.5f)
                    asoman.addRect(Rect(x0 + paso * 0.1f + dx, top - 1.3f * u, x0 + paso * 0.9f + dx, top + 0.4f * u))
                }

            }
            drawPath(asoman, a.luz(VITELA, k + 0.05f))
            drawPath(sombras, a.sombra(0.26f))
            drawPath(luces, a.claro(0.4f))
            drawPath(puntadas, a.luz(mezcla(CUERO, TINTA, 0.4f), k))
            // La camisa de pergamino más oscuro, que envuelve la parte de abajo, con su orilla desigual.
            val camisa = Path().apply {
                moveTo(0f, yCamisa + 1.5f * u)
                lineTo(w * 0.3f, yCamisa - 0.5f * u); lineTo(w * 0.62f, yCamisa + 1f * u); lineTo(w, yCamisa - 1f * u)
                lineTo(w, h); lineTo(0f, h); close()
            }
            drawPath(camisa, a.luz(mezcla(CUERNO, CUERO, 0.45f), k))
            drawPath(borde(camisa, 0f, 1.6f * u), a.claro(0.3f))
            drawRect(a.sombra(0.3f), Offset(0f, yCamisa - 1.5f * u), Size(w, 1.2f * u))
            drawRect(a.claro(0.18f), Offset(w * 0.06f, yCamisa), Size(w * 0.1f, h - yCamisa))
            drawRect(a.sombra(0.2f), Offset(w * 0.76f, yCamisa), Size(w * 0.24f, h - yCamisa))
        }
        // La correa en cruz, ancha, del color del ánimo (cuero sin teñir si no se marcó).
        val correa = v.tono?.let { a.animo(mezcla(it, PELO, if (a.oscuro) 0.05f else 0.15f), k) } ?: a.luz(CUERO, k)
        val honda = mezcla(correa, a.hondo, 0.35f)
        val yc = h * 0.27f; val gc = maxOf(7f * u, h * 0.13f)
        val xv = w * 0.5f; val gv = w * 0.26f
        drawRect(a.sombra(0.35f), Offset(xv - gv / 2 + 1f * u, 0f), Size(gv, h))
        drawRect(correa, Offset(xv - gv / 2, 0f), Size(gv, h))
        drawRect(honda, Offset(xv + gv / 2 - gv * 0.25f, 0f), Size(gv * 0.25f, h))
        drawRect(a.sombra(0.35f), Offset(0f, yc + gc), Size(w, 1.2f * u))
        drawRect(correa, Offset(0f, yc), Size(w, gc))
        drawRect(a.claro(0.32f), Offset(0f, yc), Size(w * 0.6f, 0.8f * u))
        drawRect(honda, Offset(w * 0.8f, yc), Size(w * 0.2f, gc))
        // La hebilla al frente, donde se cruzan; en los Sueños, el sello de cera con la luna sobre el nudo.
        val c = Offset(xv, yc + gc / 2)
        if (v.noche) {
            val rs = minOf(w * 0.2f, gc * 0.75f)
            drawCircle(a.sombra(0.4f), rs, c + Offset(0.8f * u, 1f * u))
            drawCircle(a.luz(GRANA, maxOf(k, 0.25f)), rs, c)
            drawCircle(if (a.oscuro) mezcla(LUNA, a.hondo, 0.25f - 0.25f * k) else LUNA, rs * 0.58f, c)
            drawCircle(a.luz(GRANA, maxOf(k, 0.25f)), rs * 0.48f, c + Offset(rs * 0.26f, -rs * 0.16f))
        } else {
            val hb = Rect(c.x - gv * 0.45f, c.y - gc * 0.62f, c.x + gv * 0.45f, c.y + gc * 0.62f)
            drawRect(a.sombra(0.4f), hb.topLeft + Offset(0.8f * u, 1f * u), hb.size)
            drawRect(a.luz(LATON, k), hb.topLeft, hb.size)
            drawRect(correa, Offset(hb.left + 1.3f * u, hb.top + 1.3f * u), Size(hb.width - 2.6f * u, hb.height - 2.6f * u))
            drawLine(a.luz(LATON, k), Offset(c.x, hb.top + 1f * u), Offset(c.x, hb.bottom - 1f * u), 1f * u)
            drawRect(if (a.oscuro) a.filo(0.8f * k) else a.claro(0.5f), hb.topLeft, Size(hb.width, 0.8f * u))
        }
        // La papeleta con el día, cosida a la camisa con dos puntadas.
        val et = Rect(w * 0.08f, yCamisa + 4f * u, w * 0.92f, yCamisa + 23f * u)
        drawRect(a.sombra(0.42f), et.topLeft + Offset(0.9f * u, 1.1f * u), et.size)
        drawRect(a.etiqueta(k), et.topLeft, et.size)
        drawRect(a.sombra(0.12f), Offset(et.right - et.width * 0.15f, et.top), Size(et.width * 0.15f, et.height))
        val hilo = a.luz(mezcla(CUERO, TINTA, 0.4f), k)
        drawLine(hilo, Offset(et.left + 1.6f * u, et.top - 1f * u), Offset(et.left + 1.6f * u, et.top + 2.4f * u), 0.8f * u)
        drawLine(hilo, Offset(et.right - 1.6f * u, et.top - 1f * u), Offset(et.right - 1.6f * u, et.top + 2.4f * u), 0.8f * u)
        textoEn(medidor, v.dia, Rect(et.left + 2f * u, et.top + 0.5f * u, et.right - 2f * u, et.bottom), LETRA, a.tinta, 14.5f, peso = FontWeight.Bold)
        // El mes, a pluma en la camisa, si cabe.
        val mes = Rect(w * 0.08f, et.bottom + 1.5f * u, w * 0.92f, h - 1.5f * u)
        if (mes.height > 6f * u) textoEn(medidor, v.mes, mes, LETRA, if (a.oscuro) a.etiqueta(k).copy(alpha = 0.8f) else TINTA.copy(alpha = 0.75f), 7.5f, minimo = 4.5f)
        if (a.oscuro && k > 0.08f) drawRect(a.filo(0.4f * k), Offset(1f * u, 2f * u), Size(1f * u, h - 2f * u))
    }
}
