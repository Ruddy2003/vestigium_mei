package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * La paleta del estante de Scriptorium (sale del cuadro del copista y de la cal, el almagre y el pergamino del tema): la
 * arenisca rosada del muro de la celda del armarius, el nogal del armario, el hierro, el pergamino, la badana y la luz.
 */
internal object PaletaMedieval {
    /** La arenisca rosada del muro: su cara, la luz, lo hondo; el mortero de cal de las juntas. */
    val ARENISCA = Color(0xFFC39B80)
    val ARENISCA_CLARA = Color(0xFFDEC2A4)
    val ARENISCA_OSC = Color(0xFF94695A)
    val MORTERO = Color(0xFFCBBDA3)
    /** La cal del derrame de la ventana. */
    val CAL = Color(0xFFEDE3CD)
    /** El nogal del armario: su cara, su luz y lo hondo de la talla. */
    val NOGAL = Color(0xFF573A2B)
    val NOGAL_LUZ = Color(0xFF86614A)
    val NOGAL_OSC = Color(0xFF2B1C15)
    /** El pino del entarimado. */
    val PINO = Color(0xFF9B7553)
    /** El hierro forjado de flejes, clavos y llaves. */
    val HIERRO = Color(0xFF2E2B2E)
    val HIERRO_LUZ = Color(0xFF7B7371)
    /** El bronce de la campanilla. */
    val BRONCE = Color(0xFFA7793B)
    /** El pergamino y su cara amarilla, la tinta y la rúbrica (el almagre de los títulos). */
    val PERGAMINO = Color(0xFFEFE3C5)
    val PERG_OSC = Color(0xFFD3BD8F)
    val TINTA = Color(0xFF241710)
    val RUBRICA = Color(0xFF9E2B22)
    /** La badana (cuero curtido al alumbre, sin teñir), el cuero pardo y el lino crudo. */
    val BADANA = Color(0xFFE2D3B2)
    val CUERO = Color(0xFF7A4C2F)
    val LINO = Color(0xFFC9BEA5)
    /** El barro de la alcuza. */
    val BARRO = Color(0xFFAE6648)
    /** Las ceras de los sellos: la natural, la roja, la verde y la parda. */
    val CERAS = listOf(Color(0xFFC59B4A), Color(0xFF962A22), Color(0xFF4F6A3C), Color(0xFF6E4B2C))
    /** Los cielos de la ventana: el día, la noche y el anochecer (abajo y arriba). */
    val CIELO = Color(0xFFDDE8EE)
    val CIELO_NOCHE = Color(0xFF1B2444)
    val CREPUSCULO = Color(0xFFE39C6A)
    val CREPUSCULO_ALTO = Color(0xFF6C5A8C)
    /** El aire de la noche, el de los Sueños y el anochecer de los Sueños en claro; la llama del candil y la luna. */
    val NOCHE = Color(0xFF110E18)
    val NOCHE_SUENOS = Color(0xFF0E1231)
    val ANOCHECER = Color(0xFF2E2B55)
    val LLAMA = Color(0xFFFFB760)
    val FUEGO = Color(0xFFFFE1A8)
    val LUNA = Color(0xFFE4E8F2)
    /** Lo hondo del tinte: el ánimo se tiñe hacia aquí para que sea cuero o lana y no pastel. */
    val TINTE_HONDO = Color(0xFF231519)

    val LETRA: FontFamily = LetraExtra.cardo
}

/**
 * El aire del momento en el estante de Scriptorium: de día (la luz de la ventana), de noche (el modo oscuro: solo el candil)
 * y en los Sueños ([noche]: al anochecer en claro, la noche más azul en oscuro, con la luna en la ventana). De él salen el
 * color de cada cosa según cuánta luz del candil le llega, la sombra, la luz y el filo.
 */
internal class AireMedieval(val oscuro: Boolean, val noche: Boolean) {
    private val p = PaletaMedieval
    /** Si arde el candil. */
    val lumbre: Boolean get() = oscuro || noche
    /** El color en que se hunde lo que no se alumbra. */
    val hondo: Color get() = if (oscuro) (if (noche) p.NOCHE_SUENOS else p.NOCHE) else SOMBRA_MUEBLE

    fun luz(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(mezcla(c, p.ANOCHECER, 0.4f - 0.2f * k), p.LLAMA, 0.1f * k) else c
        return mezcla(mezcla(c, hondo, 0.9f - 0.92f * k.pow(0.85f)), p.LLAMA, 0.3f * k)
    }
    fun sombra(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)
    fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.12f else if (noche) a * 0.5f else a)
    /** El filo del candil en lo que lo mira; de día no hay. */
    fun filo(a: Float): Color = p.LLAMA.copy(alpha = if (oscuro) a.coerceIn(0f, 1f) else if (noche) (a * 0.4f).coerceIn(0f, 1f) else 0f)
    /** El color del ánimo: de noche se apaga menos que lo demás, para que se siga viendo en la repisa más lejana. */
    fun animo(t: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(t, p.ANOCHECER, 0.2f) else t
        return mezcla(mezcla(t, hondo, 0.78f - 0.74f * k.pow(0.8f)), p.LLAMA, 0.14f * k)
    }
    /** El pergamino donde va el día: casi no se apaga, para que el número se lea siempre. */
    fun etiqueta(cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(p.PERGAMINO, p.ANOCHECER, 0.12f) else p.PERGAMINO
        return mezcla(mezcla(p.PERGAMINO, hondo, 0.42f - 0.36f * k), p.LLAMA, 0.16f * k)
    }
    val tinta: Color get() = p.TINTA
}

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoMedieval(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** Un vano de medio punto: de [x0] a [x1], con la clave en [arriba] y el piso en [abajo]. */
internal fun medioPuntoMedieval(x0: Float, x1: Float, arriba: Float, abajo: Float) = Path().apply {
    val r = (x1 - x0) / 2
    moveTo(x0, abajo); lineTo(x0, arriba + r)
    arcTo(Rect(x0, arriba, x1, arriba + 2 * r), 180f, 180f, false)
    lineTo(x1, abajo); close()
}

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz ([hacia] -1 si la luz viene de la derecha). */
internal fun DrawScope.contactoMedieval(centro: Offset, ancho: Float, alto: Float, a: AireMedieval, fuerza: Float = 0.34f, hacia: Float = 1f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.3f else fuerza), Offset(centro.x - ancho * (0.5f - 0.08f * hacia), centro.y - alto / 2), Size(ancho, alto))

/** Un clavo de forja visto de frente: su sombra, la cabeza y el punto de luz. */
internal fun DrawScope.clavoMedieval(p: Offset, r: Float, color: Color, a: AireMedieval, filo: Float = 0f) {
    drawCircle(a.sombra(0.4f), r * 1.05f, p + Offset(r * 0.35f, r * 0.45f))
    drawCircle(color, r, p)
    drawCircle(a.claro(0.5f), r * 0.36f, p - Offset(r * 0.3f, r * 0.3f))
    if (a.oscuro && filo > 0.05f) drawCircle(a.filo(0.7f * filo), r * 0.4f, p + Offset(r * 0.25f, -r * 0.25f))
}

/**
 * El dibujo del estante del diario de Scriptorium (lo usa `EstanteMedieval`, en `BibliotecaMedievo.kt`): el armario de nogal
 * de la celda del armarius, en una abadía benedictina hacia 1150, contra un muro de sillarejo de arenisca rosada con su
 * ventana abocinada arriba a la izquierda. Todo el dibujo sale de [estante] y de [volumen]; la luz de la noche de los dos sale
 * de la misma curva, [alcance]: la del candil de hierro colgado a la derecha. Los objetos están en [PiezasEstanteMedieval] y
 * las entradas en [VolumenEstanteMedieval].
 */
internal object MuebleEstanteMedieval {
    private val P = PaletaMedieval

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.52f
    const val BASE = 0.2f
    const val LADO = 0.07f
    val ALTO_REPISA: Dp = 114.dp
    val TABLA: Dp = 18.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (24 + (palabras / 40).coerceAtMost(20)).dp

    /** Cuál de los tres es una entrada: 0, el códice románico; 1, el libro en su camisa; 2, el legajo de cartas selladas. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod(semilla xor (semilla ushr 7), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** El códice, el más alto; el libro en camisa, mediano; el legajo, el más bajo. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.86f + 0.1f * azar(semilla, 1)
        1 -> 0.74f + 0.1f * azar(semilla, 1)
        else -> 0.56f + 0.1f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde el candil, en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val LAMP_X = 0.845f
    const val LAMP_Y = 0.262f
    /** El centro de la ventana, en anchos. */
    const val VENT_X = 0.165f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    private const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    private const val FILA = 132f
    /** En la base: dónde empieza el suelo, en fracción de su alto. */
    private const val SUELO = 0.52f

    /**
     * La curva de la luz del candil, la misma para el mueble y para los volúmenes: cuánta llega (de 1 a 0) a un punto que está
     * a [dx] y [dy] de la llama, en anchos del mueble. Cae deprisa: el candil alumbra el rincón de la derecha y la repisa de
     * arriba; en un estante corto se acaba antes, para que la última repisa quede siempre en penumbra.
     */
    fun alcance(dx: Float, dy: Float, repisas: Int): Float {
        val radio = 0.85f + 0.1f * repisas.coerceIn(2, 5)
        return (1f - sqrt(dx * dx + dy * dy) / radio).coerceIn(0f, 1f).pow(1.3f)
    }

    /** La luz del candil en el muro de arriba: el sillarejo se la come antes, y el rincón de la ventana queda en lo oscuro. */
    fun alcanceMuro(dx: Float, dy: Float): Float = (1f - sqrt(dx * dx * 1.2f + dy * dy) / 0.82f).coerceIn(0f, 1f).pow(1.2f)

    /** La luz que llega al cuerpo del mueble: [pos] cuenta repisas desde la de arriba (1.5 es media altura de la segunda). */
    fun cercaCuerpo(pos: Float, repisas: Int, fx: Float): Float =
        alcance(fx - LAMP_X, CORONA - LAMP_Y + pos * FILA / ANCHO_NOMINAL, repisas)

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireMedieval(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val b = d.base
        val suelo = b.top + b.height * SUELO
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * u
        val llama = Offset(w * LAMP_X, w * LAMP_Y)
        val cerca: (Offset) -> Float = { p ->
            if (p.y <= ch) alcance((p.x - llama.x) / w, (p.y - llama.y) / w, n) else cercaCuerpo((p.y - ch) / maxOf(1f, fila), n, p.x / w)
        }
        // Hacia dónde cae la sombra de algo en [p]: de día, a la derecha y abajo; de noche, lejos de la llama.
        val caida: (Offset, Float) -> Offset = { p, largo ->
            if (!oscuro) Offset(largo * 1.15f, largo * 0.8f)
            else { val v = p - llama; v / v.getDistance().coerceAtLeast(1f) * (largo * 2.4f) }
        }
        val gC = w * 0.036f            // el alto de los canecillos
        val gT = w * 0.044f            // el canto del tablero de arriba
        val fT = w * 0.015f            // lo que se ve de su cara de arriba
        val canto = ch - gC            // donde acaba el canto del tablero
        val apoyo = canto - gT - fT * 0.45f

        with(MuroEstanteMedieval) { muro(a, apoyo) { p -> alcanceMuro((p.x - llama.x) / w, (p.y - llama.y) / w) }; ventana(a) }
        if (a.lumbre) {
            val f = if (oscuro) 1f else 0.45f
            mancha(llama, w * 0.62f, P.LLAMA.copy(alpha = 0.2f * f))
            mancha(llama, w * 0.24f, P.FUEGO.copy(alpha = 0.3f * f))
        }

        // ---- el entarimado
        with(MuroEstanteMedieval) { entarimado(a, suelo, w, h, cerca) }

        // ---- el fondo de cada repisa, las tablas con sus rótulos y los costados
        d.repisas.forEachIndexed { k, r -> fondoRepisa(k, r, d.ocupado.getOrNull(k), a, cerca) }
        d.tablas.forEachIndexed { k, r -> tabla(r, 910 + 7 * k, a) { x -> cerca(Offset(x, r.center.y)) } }
        d.placas.forEach { p -> cartela(p, medidor, a, cerca(p.r.center)) }
        costado(d.izquierda, false, d, a, n, ch, fila, suelo)
        costado(d.derecha, true, d, a, n, ch, fila, suelo)

        // ---- lo que hay en los huecos libres de las repisas
        with(PiezasEstanteMedieval) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                huecoLibre(k, r, oc, a, cerca(Offset((oc + r.right) / 2, r.center.y)))
            }
        }

        // ---- la base: el travesaño con su arco rebajado entre los pies
        zocalo(Rect(d.izquierda.right, b.top, d.derecha.left, suelo), a, cerca)

        // ---- el tablero de arriba sobre sus canecillos, y las cabezas de los pies derechos
        canecillos(Rect(0f, canto, w, ch), a, cerca)
        tablero(Rect(-2f * u, canto - gT, w + 2f * u, canto), fT, a, cerca)
        cabeza(Offset(d.izquierda.center.x, canto - gT - fT), d.izquierda.width, a, cerca, caida)
        cabeza(Offset(d.derecha.center.x, canto - gT - fT), d.derecha.width, a, cerca, caida)

        // ---- el rayo de la ventana, lo de arriba, el candil y lo del pie
        with(MuroEstanteMedieval) { rayo(a, apoyo) }
        with(PiezasEstanteMedieval) {
            arriba(a, apoyo, cerca, caida)
            candil(a)
            alPie(a, suelo, h, d, cerca)
            if (a.lumbre) llamaCandil(a)
        }
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireMedieval(oscuro, v.noche)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val k = if (a.lumbre) cercaCuerpo(v.repisa + 0.55f, v.repisas, fx) else 1f
        // De qué lado le llega la luz: de día, de la ventana (izquierda); de noche, del candil.
        val hacia = if (a.oscuro && fx > LAMP_X) -1f else if (a.oscuro) 1f else -1f
        with(VolumenEstanteMedieval) {
            when (clase(v.semilla)) {
                0 -> codice(v, medidor, a, k, hacia)
                1 -> camisa(v, medidor, a, k, hacia)
                else -> legajo(v, medidor, a, k, hacia)
            }
        }
    }

    // ---------------------------------------------------------------- el armario

    /**
     * El fondo de una repisa: tablas de nogal verticales con las huellas de la azuela, la sombra de la tabla de arriba, el
     * costado del lado de la sombra más hondo y la sombra del último volumen.
     */
    private fun DrawScope.fondoRepisa(k: Int, r: Rect, oc: Float?, a: AireMedieval, cerca: (Offset) -> Float) {
        val u = density
        clipRect(r.left, r.top, r.right, r.bottom) {
            val ancho = 23f * u
            var x = r.left; var i = 0
            while (x < r.right) {
                val t0 = azar(1401 + k, i)
                val tono = if (t0 > 0.78f) mezcla(P.NOGAL, P.NOGAL_LUZ, 0.12f) else mezcla(P.NOGAL, P.NOGAL_OSC, 0.3f + 0.4f * t0)
                val partes = if (a.lumbre) 3 else 1
                for (t in 0 until partes) {
                    val ya = r.top + r.height * t / partes
                    drawRect(a.luz(tono, cerca(Offset(x + ancho / 2, ya + r.height / (2 * partes)))), Offset(x, ya), Size(ancho + 0.6f, r.height / partes + 0.6f))
                }
                drawLine(a.sombra(0.45f), Offset(x, r.top), Offset(x, r.bottom), 1f * u)
                drawLine(a.claro(0.12f), Offset(x + 1.2f * u, r.top), Offset(x + 1.2f * u, r.bottom), 0.7f * u)
                // Las huellas de la azuela: golpes cortos en curva.
                for (g in 0 until 4) {
                    val gy = r.top + r.height * (0.2f + 0.2f * g + 0.08f * azar(1402 + k, i * 7 + g))
                    val gx = x + ancho * (0.2f + 0.5f * azar(1403 + k, i * 7 + g))
                    drawArc(a.sombra(0.16f), 200f, 140f, false, Offset(gx, gy), Size(ancho * 0.36f, 4f * u), style = Stroke(0.7f * u))
                }
                // Algún nudo de la madera.
                if (azar(1405 + k, i) > 0.72f) {
                    val nc = Offset(x + ancho * (0.3f + 0.4f * azar(1406 + k, i)), r.top + r.height * (0.3f + 0.5f * azar(1407 + k, i)))
                    drawOval(a.sombra(0.3f), Offset(nc.x - 2.2f * u, nc.y - 3.4f * u), Size(4.4f * u, 6.8f * u))
                    drawOval(a.sombra(0.2f), Offset(nc.x - 3.8f * u, nc.y - 6f * u), Size(7.6f * u, 12f * u), style = Stroke(0.6f * u))
                }
                x += ancho; i++
            }
            vetas(r, P.NOGAL_OSC.copy(alpha = if (a.oscuro) 0.35f else 0.45f), 1404 + k, densidad = 0.45f, vertical = true, grosor = 0.7f)
            // La sombra de la tabla de arriba: honda junto a ella y deshecha hacia abajo.
            val f = if (a.oscuro) 1.2f else 1f
            drawRect(a.sombra(0.42f * f), r.topLeft, Size(r.width, 8f * u))
            drawRect(a.sombra(0.22f * f), r.topLeft, Size(r.width, 20f * u))
            drawRect(a.sombra(0.1f * f), r.topLeft, Size(r.width, 36f * u))
            // El lado del costado que queda en sombra.
            if (!a.oscuro) {
                drawRect(a.sombra(0.3f), r.topLeft, Size(8f * u, r.height))
                pinceladas(Rect(r.left + 8f * u, r.top + r.height * 0.3f, r.left + r.width * 0.45f, r.bottom), P.NOGAL_LUZ, 1411 + k, 10, u, if (a.noche) 0.05f else 0.1f)
                drawRect(a.sombra(0.16f), Offset(r.right - 12f * u, r.top), Size(12f * u, r.height))
            } else {
                drawRect(a.sombra(0.2f), r.topLeft, Size(14f * u, r.height))
                drawRect(a.sombra(0.2f), Offset(r.right - 6f * u, r.top), Size(6f * u, r.height))
            }
            // La sombra que arroja el último volumen sobre el fondo, a su derecha (de día).
            if (!a.oscuro && oc != null && oc > r.left + 6f * u) {
                val largo = 13f * u
                drawPath(poligonoMedieval(oc - 2f * u, r.bottom, oc - 2f * u, r.top + r.height * 0.22f, oc + largo, r.top + r.height * 0.32f, oc + largo, r.bottom), a.sombra(0.28f))
            }
        }
    }

    /** Una tabla: su cara de arriba, vista apenas, el canto grueso con el chaflán de abajo, los clavos y algún golpe. */
    private fun DrawScope.tabla(r: Rect, semilla: Int, a: AireMedieval, cerca: (Float) -> Float) {
        val u = density
        val fondo = 3.5f * u
        val tramos = if (a.lumbre) 26 else 1
        for (t in 0 until tramos) {
            val xa = r.left + r.width * t / tramos; val xb = r.left + r.width * (t + 1) / tramos + 0.6f
            val k = cerca((xa + xb) / 2)
            drawRect(a.luz(mezcla(P.NOGAL, P.NOGAL_LUZ, 0.6f), k), Offset(xa, r.top - fondo), Size(xb - xa, fondo))
            drawRect(a.luz(P.NOGAL, k), Offset(xa, r.top), Size(xb - xa, r.height))
            drawRect(a.luz(mezcla(P.NOGAL, P.NOGAL_OSC, 0.45f), k), Offset(xa, r.bottom - 4.5f * u), Size(xb - xa, 4.5f * u))
            if (a.oscuro) drawRect(a.filo(0.05f + 0.6f * k), Offset(xa, r.top - fondo), Size(xb - xa, 1.1f * u))
        }
        clipRect(r.left, r.top, r.right, r.bottom) {
            vetas(r, P.NOGAL_OSC.copy(alpha = 0.5f), semilla, densidad = 0.7f, grosor = 0.7f)
            drawRect(a.claro(0.3f), Offset(r.left, r.top), Size(r.width, 1.2f * u))
            drawRect(a.sombra(0.35f), Offset(r.left, r.bottom - 4.8f * u), Size(r.width, 0.8f * u))
            // El bocel sogueado tallado a lo largo del canto: los cabos de la soga, cada uno con su luz y su sombra.
            val yt = r.top + 3.4f * u; val yb = r.bottom - 6.4f * u
            drawRect(a.sombra(0.3f), Offset(r.left, yt - 0.6f * u), Size(r.width, yb - yt + 1.2f * u))
            var x = r.left + 2f * u
            while (x < r.right) {
                val k = cerca(x)
                drawLine(a.sombra(0.4f), Offset(x + 0.8f * u, yb + 0.6f * u), Offset(x + 4.2f * u, yt + 0.8f * u), 3f * u, StrokeCap.Round)
                drawLine(a.luz(mezcla(P.NOGAL, P.NOGAL_LUZ, 0.45f), k), Offset(x, yb), Offset(x + 3.4f * u, yt), 2.8f * u, StrokeCap.Round)
                drawLine(if (a.oscuro) a.filo(0.35f * k) else a.claro(0.3f), Offset(x - 0.6f * u, yb - 1.2f * u), Offset(x + 2f * u, yt + 0.4f * u), 0.7f * u)
                x += 4.6f * u
            }
            // Los golpes y el roce de las manos en el canto.
            for (i in 0 until 3) {
                val mx = r.left + r.width * (0.12f + 0.76f * azar(semilla, 9 + i))
                drawPath(poligonoMedieval(mx, r.top, mx + 4f * u, r.top, mx + 1.5f * u, r.top + 2.4f * u), a.sombra(0.4f))
            }
            drawOval(a.claro(0.1f), Offset(r.left + r.width * 0.3f, r.top + 2f * u), Size(r.width * 0.3f, r.height * 0.4f))
        }
        drawLine(a.sombra(0.4f), Offset(r.left, r.top - fondo), Offset(r.right, r.top - fondo), 0.7f * u)
        drawRect(a.sombra(0.42f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
    }

    /** El rótulo de un mes: una cédula de pergamino de borde desigual, pautada en almagre, clavada al canto con dos clavos. */
    private fun DrawScope.cartela(p: PlacaEstante, medidor: TextMeasurer, a: AireMedieval, k: Float) {
        val u = density
        val r = p.r
        val s = p.texto.length * 31
        val ced = poligonoMedieval(
            r.left - 4f * u, r.top + 0.8f * u * azar(s, 1), r.center.x, r.top - 0.6f * u, r.right + 4f * u, r.top + 0.5f * u * azar(s, 2),
            r.right + 3f * u + 1.2f * u * azar(s, 3), r.center.y, r.right + 4f * u, r.bottom - 0.4f * u, r.center.x + r.width * 0.2f, r.bottom + 0.7f * u,
            r.left - 4f * u, r.bottom, r.left - 3f * u - 1f * u * azar(s, 4), r.center.y,
        )
        drawPath(ced, a.sombra(0.42f), style = Stroke(2.4f * u))
        drawPath(ced, a.etiqueta(maxOf(k, 0.15f)))
        clipPath(ced) {
            pinceladas(Rect(r.left - 4f * u, r.top, r.right + 4f * u, r.bottom), a.sombra(1f), 1421 + s, 8, u, 0.06f)
            drawRect(a.claro(0.5f), Offset(r.left - 4f * u, r.top - 1f * u), Size(r.width + 8f * u, 1.4f * u))
            drawRect(a.sombra(0.12f), Offset(r.right - r.width * 0.12f, r.top - 1f * u), Size(r.width * 0.12f + 5f * u, r.height + 2f * u))
        }
        val roja = if (a.oscuro) mezcla(P.RUBRICA, a.hondo, 0.2f) else P.RUBRICA
        drawLine(roja.copy(alpha = 0.6f), Offset(r.left + 7f * u, r.top + 2.2f * u), Offset(r.right - 7f * u, r.top + 2.2f * u), 0.6f * u)
        drawLine(roja.copy(alpha = 0.6f), Offset(r.left + 7f * u, r.bottom - 2.2f * u), Offset(r.right - 7f * u, r.bottom - 2.2f * u), 0.6f * u)
        listOf(r.left + 2f * u, r.right - 2f * u).forEach { x -> clavoMedieval(Offset(x, r.center.y), 1.4f * u, a.luz(P.HIERRO_LUZ, maxOf(k, 0.3f)), a, k) }
        textoEn(medidor, p.texto, Rect(r.left + 7f * u, r.top + 2.5f * u, r.right - 7f * u, r.bottom - 2.5f * u), P.LETRA, a.tinta, 11f, minimo = 5f)
    }

    /**
     * Un pie derecho: el tablón de nogal con sus chaflanes, que baja hasta el suelo; en cada tabla, el fleje de hierro que
     * la abraza y acaba en dos rizos, con sus clavos.
     */
    private fun DrawScope.costado(r: Rect, derecho: Boolean, d: DisposicionEstante, a: AireMedieval, repisas: Int, ch: Float, fila: Float, suelo: Float) {
        val u = density; val w = size.width
        val fx = r.center.x / w
        fun k(y: Float) = cercaCuerpo((y - ch) / maxOf(1f, fila), repisas, fx)
        val cuerpo = Rect(r.left, r.top, r.right, suelo)
        val tramos = if (a.lumbre) (cuerpo.height / maxOf(1f, fila * 0.25f)).toInt().coerceIn(4, 80) else 1
        for (t in 0 until tramos) {
            val ya = cuerpo.top + cuerpo.height * t / tramos; val yb = cuerpo.top + cuerpo.height * (t + 1) / tramos + 0.6f
            drawRect(a.luz(P.NOGAL, k((ya + yb) / 2)), Offset(r.left, ya), Size(r.width, yb - ya))
        }
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            vetas(cuerpo, P.NOGAL_OSC.copy(alpha = 0.5f), if (derecho) 1431 else 1432, densidad = 0.8f, vertical = true, grosor = 0.7f)
            pinceladas(cuerpo, P.NOGAL_LUZ, if (derecho) 1433 else 1434, 12, u, if (a.oscuro) 0.03f else 0.08f)
        }
        // Los chaflanes de las dos aristas: el de la luz, claro; el otro, en sombra.
        val ch2 = 3f * u
        drawRect(if (!a.oscuro) a.claro(0.32f) else a.filo(0.15f), Offset(r.left, r.top), Size(ch2, cuerpo.height))
        drawRect(a.sombra(0.36f), Offset(r.right - ch2, r.top), Size(ch2, cuerpo.height))
        if (derecho) drawRect(a.sombra(0.3f), Offset(r.left, r.top), Size(1.6f * u, cuerpo.height))
        else drawRect(a.sombra(0.3f), Offset(r.right - 1.6f * u, r.top), Size(1.6f * u, cuerpo.height))
        // Los flejes, en cada tabla.
        d.tablas.forEach { t ->
            val y = t.center.y
            fleje(r, y, derecho, a, k(y))
        }
    }

    /** El fleje de hierro que abraza el pie derecho y la tabla, con su rizo partido sobre el canto. */
    private fun DrawScope.fleje(r: Rect, y: Float, derecho: Boolean, a: AireMedieval, k: Float) {
        val u = density
        val hacia = if (derecho) -1f else 1f
        val x0 = if (derecho) r.right else r.left
        val x1 = if (derecho) r.left - 18f * u else r.right + 18f * u
        val g = 4.4f * u
        drawLine(a.sombra(0.45f), Offset(x0, y + 1.4f * u), Offset(x1 + hacia * 1f * u, y + 1.4f * u), g)
        drawLine(a.luz(P.HIERRO, k), Offset(x0, y), Offset(x1, y), g)
        drawLine(a.claro(0.3f), Offset(x0, y - g * 0.4f), Offset(x1, y - g * 0.4f), 0.6f * u)
        // El rizo partido: dos volutas que se abren, arriba y abajo.
        val c = Offset(x1, y)
        for (s in listOf(-1f, 1f)) {
            val p = Path().apply {
                moveTo(c.x, c.y)
                cubicTo(c.x + hacia * 4f * u, c.y + s * 1f * u, c.x + hacia * 6f * u, c.y + s * 5f * u, c.x + hacia * 3f * u, c.y + s * 6.4f * u)
                cubicTo(c.x + hacia * 1f * u, c.y + s * 7f * u, c.x, c.y + s * 5f * u, c.x + hacia * 1.6f * u, c.y + s * 4f * u)
            }
            drawPath(p, a.sombra(0.4f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            drawPath(p, a.luz(P.HIERRO, k), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        }
        clavoMedieval(Offset((x0 + x1) / 2 - hacia * 2f * u, y), 1.1f * u, a.luz(P.HIERRO_LUZ, k), a, k)
        clavoMedieval(Offset(x1 - hacia * 3f * u, y), 0.9f * u, a.luz(P.HIERRO_LUZ, k), a, k)
        if (a.oscuro) drawLine(a.filo(0.5f * k), Offset(x0, y - g * 0.45f), Offset(x1, y - g * 0.45f), 0.7f * u)
    }

    /**
     * La cabeza de un pie derecho, que asoma sobre el tablero: el taco de nogal con su chaflán, un anillo y un pomo torneado
     * en bola, con su luz, su sombra y la sombra que deja en el muro.
     */
    private fun DrawScope.cabeza(pie: Offset, an: Float, a: AireMedieval, cerca: (Offset) -> Float, caida: (Offset, Float) -> Offset) {
        val u = density; val w = size.width
        val k = cerca(pie - Offset(0f, w * 0.03f))
        val bloque = Rect(pie.x - an / 2, pie.y - an * 0.34f, pie.x + an / 2, pie.y + 2f * u)
        val cuello = Rect(pie.x - an * 0.17f, bloque.top - an * 0.2f, pie.x + an * 0.17f, bloque.top)
        val rb = an * 0.36f
        val bola = Offset(pie.x, cuello.top - rb * 0.9f)
        // La sombra en el muro.
        val c = caida(bola, w * 0.01f).let { Offset(it.x, minOf(it.y, w * 0.004f)) }
        val sombra = a.sombra(if (a.oscuro) 0.34f else 0.2f)
        drawCircle(sombra, rb, bola + c)
        drawRect(sombra, Offset(bloque.left + c.x, bloque.top + c.y), Size(an, bloque.height))
        // El taco.
        drawRect(a.luz(P.NOGAL, k), bloque.topLeft, bloque.size)
        clipRect(bloque.left, bloque.top, bloque.right, bloque.bottom) {
            vetas(bloque, P.NOGAL_OSC.copy(alpha = 0.45f), 1441 + pie.x.toInt(), densidad = 0.8f, vertical = true, grosor = 0.6f)
        }
        drawRect(a.claro(0.3f), bloque.topLeft, Size(3f * u, bloque.height))
        drawRect(a.sombra(0.34f), Offset(bloque.right - 4f * u, bloque.top), Size(4f * u, bloque.height))
        drawRect(a.luz(mezcla(P.NOGAL, P.NOGAL_LUZ, 0.6f), k), Offset(bloque.left, bloque.top - 1.6f * u), Size(an, 2.4f * u))
        // El anillo, el cuello y la bola torneados.
        drawRoundRect(a.luz(P.NOGAL_LUZ, k), Offset(pie.x - an * 0.36f, bloque.top - 3.2f * u), Size(an * 0.72f, 3f * u), androidx.compose.ui.geometry.CornerRadius(1.5f * u))
        drawRect(a.luz(P.NOGAL, k), cuello.topLeft, cuello.size)
        drawRect(a.sombra(0.3f), Offset(cuello.right - cuello.width * 0.4f, cuello.top), Size(cuello.width * 0.4f, cuello.height))
        drawCircle(a.luz(P.NOGAL, k), rb, bola)
        clipPath(Path().apply { addOval(Rect(bola.x - rb, bola.y - rb, bola.x + rb, bola.y + rb)) }) {
            drawCircle(a.sombra(0.32f), rb, bola + Offset(rb * 0.45f, rb * 0.35f))
            drawCircle(a.luz(P.NOGAL_LUZ, k), rb * 0.5f, bola - Offset(rb * 0.25f, rb * 0.3f))
            drawCircle(a.claro(0.4f), rb * 0.18f, bola - Offset(rb * 0.4f, rb * 0.45f))
        }
        drawCircle(a.luz(P.NOGAL_LUZ, k), rb * 0.26f, bola - Offset(0f, rb * 1.05f))
        if (a.oscuro && k > 0.05f) drawArc(a.filo(0.7f * k), 250f, 120f, false, Offset(bola.x - rb, bola.y - rb), Size(2 * rb, 2 * rb), style = Stroke(1.1f * u))
    }

    /** El tablero de arriba: su cara, que se ve apenas, y el canto con su chaflán, que vuela sobre los canecillos. */
    private fun DrawScope.tablero(r: Rect, fondo: Float, a: AireMedieval, cerca: (Offset) -> Float) {
        val u = density
        val tramos = if (a.lumbre) 30 else 1
        for (t in 0 until tramos) {
            val xa = r.left + r.width * t / tramos; val xb = r.left + r.width * (t + 1) / tramos + 0.6f
            val k = cerca(Offset((xa + xb) / 2, r.top))
            drawRect(a.luz(mezcla(P.NOGAL, P.NOGAL_LUZ, 0.55f), k), Offset(xa, r.top - fondo), Size(xb - xa, fondo))
            drawRect(a.luz(P.NOGAL, k), Offset(xa, r.top), Size(xb - xa, r.height))
            drawRect(a.luz(mezcla(P.NOGAL, P.NOGAL_OSC, 0.5f), k), Offset(xa, r.bottom - r.height * 0.3f), Size(xb - xa, r.height * 0.3f))
            if (a.oscuro) drawRect(a.filo(0.06f + 0.6f * k), Offset(xa, r.top - fondo), Size(xb - xa, 1.2f * u))
        }
        clipRect(r.left, r.top - fondo, r.right, r.bottom) {
            vetas(Rect(r.left, r.top - fondo, r.right, r.top), P.NOGAL_OSC.copy(alpha = 0.3f), 1451, densidad = 0.9f, grosor = 0.5f)
            vetas(r, P.NOGAL_OSC.copy(alpha = 0.45f), 1452, densidad = 0.6f, grosor = 0.7f)
            drawRect(a.claro(0.32f), Offset(r.left, r.top), Size(r.width, 1.3f * u))
            drawRect(a.sombra(0.3f), Offset(r.left, r.bottom - r.height * 0.3f), Size(r.width, 0.9f * u))
            for (i in 0 until 5) {
                val mx = r.left + r.width * (0.08f + 0.84f * azar(1453, i))
                drawPath(poligonoMedieval(mx, r.top, mx + 5f * u, r.top, mx + 2f * u, r.top + 2.6f * u), a.sombra(0.35f))
            }
        }
        drawLine(a.sombra(0.4f), Offset(r.left, r.top - fondo), Offset(r.right, r.top - fondo), 0.7f * u)
        drawRect(a.sombra(0.45f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
    }

    /**
     * La fila de canecillos de rollos bajo el tablero, como los de los aleros de la iglesia: cada uno, tres rollos que
     * se ven de frente, con la luz arriba y la sombra abajo; entre ellos, el friso del armario en sombra.
     */
    private fun DrawScope.canecillos(r: Rect, a: AireMedieval, cerca: (Offset) -> Float) {
        val u = density; val w = size.width
        val tramos = if (a.lumbre) 16 else 1
        for (t in 0 until tramos) {
            val xa = r.left + r.width * t / tramos; val xb = r.left + r.width * (t + 1) / tramos + 0.6f
            drawRect(a.luz(mezcla(P.NOGAL, P.NOGAL_OSC, 0.55f), cerca(Offset((xa + xb) / 2, r.center.y))), Offset(xa, r.top), Size(xb - xa, r.height))
        }
        drawRect(a.sombra(0.45f), r.topLeft, Size(r.width, r.height * 0.35f))
        val an = w * 0.034f
        val paso = w * 0.074f
        var x = r.left + w * 0.085f
        while (x + an < r.right - w * 0.06f) {
            val k = cerca(Offset(x + an / 2, r.center.y))
            val alto = r.height / 3f
            for (j in 0 until 3) {
                val y0 = r.top + alto * j
                val rr = Rect(x + j * 0.6f * u, y0, x + an - j * 0.6f * u, y0 + alto)
                drawRoundRect(a.luz(mezcla(P.NOGAL, P.NOGAL_LUZ, 0.25f), k), rr.topLeft, rr.size, androidx.compose.ui.geometry.CornerRadius(alto * 0.45f))
                drawRect(a.claro(0.3f), Offset(rr.left + 1f * u, rr.top + 0.6f * u), Size(rr.width - 2f * u, alto * 0.22f))
                drawRect(a.sombra(0.35f), Offset(rr.left + 1f * u, rr.bottom - alto * 0.32f), Size(rr.width - 2f * u, alto * 0.32f))
                if (a.oscuro) drawRect(a.filo(0.45f * k), Offset(rr.left + 1f * u, rr.top + 0.4f * u), Size(rr.width - 2f * u, 0.8f * u))
            }
            // La sombra del canecillo en el friso, a su derecha.
            drawRect(a.sombra(0.3f), Offset(x + an, r.top), Size(3f * u, r.height))
            x += paso
        }
        drawRect(a.sombra(0.5f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
    }

    /** El travesaño de abajo, entre los pies, con su arco rebajado que deja ver la sombra bajo el armario. */
    private fun DrawScope.zocalo(r: Rect, a: AireMedieval, cerca: (Offset) -> Float) {
        val u = density
        if (r.height < 2f || r.width < 2f) return
        val k = cerca(r.center)
        drawRect(a.luz(mezcla(P.NOGAL_OSC, a.hondo, 0.3f), 0.2f * k), r.topLeft, r.size)
        val banda = r.height * 0.55f
        val pieza = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - 1f * u)
            lineTo(r.right - r.width * 0.06f, r.bottom - 1f * u)
            cubicTo(r.right - r.width * 0.22f, r.top + banda * 0.9f, r.left + r.width * 0.22f, r.top + banda * 0.9f, r.left + r.width * 0.06f, r.bottom - 1f * u)
            lineTo(r.left, r.bottom - 1f * u); close()
        }
        drawPath(pieza, a.luz(P.NOGAL, k))
        clipPath(pieza) {
            vetas(r, P.NOGAL_OSC.copy(alpha = 0.45f), 1461, densidad = 0.7f, grosor = 0.7f)
            drawRect(a.claro(0.3f), r.topLeft, Size(r.width, 1.4f * u))
            drawRect(a.sombra(0.3f), Offset(r.left, r.top + 3.4f * u), Size(r.width, 1f * u))
            pinceladas(r, a.sombra(1f), 1462, 10, u, 0.08f)
        }
        // El canto del arco, en sombra.
        drawPath(Path().apply {
            moveTo(r.right - r.width * 0.06f, r.bottom - 1f * u)
            cubicTo(r.right - r.width * 0.22f, r.top + banda * 0.9f, r.left + r.width * 0.22f, r.top + banda * 0.9f, r.left + r.width * 0.06f, r.bottom - 1f * u)
        }, a.claro(0.18f), style = Stroke(1.2f * u))
    }
}
