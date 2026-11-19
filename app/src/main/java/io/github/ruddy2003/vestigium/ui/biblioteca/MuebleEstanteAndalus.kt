package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.ALMAGRA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.BRONCE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CEDRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CEDRO_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CEDRO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.LLAMA
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.borde
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.corrido
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.poligono
import io.github.ruddy2003.vestigium.ui.ideario.arcoHerradura
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * El dibujo del estante del diario de Al-Ándalus (lo usa `EstanteAndalus`, en `BibliotecaMedievo.kt`): la librería de cedro
 * de un copista en la Córdoba califal, hacia el 970. Un mueble exento, de frente, arrimado a un muro de sillería a soga y
 * tizón: dos pilastras de cedro con un tallo de ataurique tallado, tablas con su canto de florecillas de cuatro pétalos, un
 * friso de ataurique bajo una cornisa que vuela sobre canecillos de rollos y, abajo, un zócalo con su arquería ciega. Arriba a
 * la izquierda, en el muro, una ventana de herradura de dovelas alternas con su alfiz y su celosía de piedra de círculos
 * secantes; de día entra el sol y dibuja la trama en el derrame y en el suelo; de noche, la luna fría, contra el candil de
 * bronce encendido sobre la cornisa. Todo el dibujo sale de [estante] y de [volumen]; la luz de la noche de los dos sale de
 * la misma curva, [alcance]. La paleta, el aire, el muro, la ventana, el suelo y el fondo de los casilleros están en
 * `MuebleEstanteAndalusMuro.kt`; los objetos, en `MuebleEstanteAndalusObjetos.kt` (los de la cornisa y el pie) y en
 * `MuebleEstanteAndalusHuecos.kt` (los de los huecos libres y la regla de cuerdas), y las entradas, en
 * `MuebleEstanteAndalusVolumenes.kt`.
 */
internal object MuebleEstanteAndalus {
    internal val LETRA: FontFamily = LetraExtra.amiri

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.62f
    const val BASE = 0.22f
    const val LADO = 0.072f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 18.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (24 + (palabras / 40).coerceAtMost(18)).dp

    /** Cuál de los tres es una entrada: 0, el códice de cuero con su solapa; 1, el estuche del rollo; 2, el legajo de hojas sueltas. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod(semilla xor (semilla ushr 7), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** El códice, mediano; el estuche, el más alto; el legajo, el más bajo. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.78f + 0.09f * azar(semilla, 1)
        1 -> 0.89f + 0.08f * azar(semilla, 1)
        else -> 0.58f + 0.08f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde el candil (la punta de la piquera), en anchos del mueble desde la esquina de arriba a la izquierda. */
    internal const val LAMP_X = 0.612f
    internal const val LAMP_Y = 0.347f
    /** Dónde pisa lo que está sobre la cornisa, en anchos. */
    internal const val APOYO = 0.455f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    private const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    private const val FILA = 130f
    /** En la base: dónde empieza el suelo, en fracción de su alto. */
    private const val SUELO = 0.46f

    /**
     * La curva de la luz del candil, la misma para el mueble y para los volúmenes: cuánta llega (de 1 a 0) a un punto que está a
     * [dx] y [dy] de la llama, en anchos del mueble. En un estante corto la luz se acaba antes, para que la última repisa quede
     * siempre en penumbra; de cinco repisas en adelante el alcance ya no crece.
     */
    fun alcance(dx: Float, dy: Float, repisas: Int): Float {
        val radio = 0.9f + 0.1f * repisas.coerceIn(2, 5)
        return (1f - sqrt(dx * dx * 1.2f + dy * dy) / radio).coerceIn(0f, 1f).pow(1.3f)
    }

    /** La luz que llega al cuerpo del mueble: [pos] cuenta repisas desde la de arriba (1.5 es media altura de la segunda). */
    fun cercaCuerpo(pos: Float, repisas: Int, fx: Float): Float =
        alcance(fx - LAMP_X, CORONA - LAMP_Y + pos * FILA / ANCHO_NOMINAL, repisas)

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w <= 1f || h <= 1f) return
        val a = AireAndalus(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val b = d.base
        val suelo = b.top + b.height * SUELO
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * u
        val llama = Offset(w * LAMP_X, w * LAMP_Y)
        // La luz en un punto del lienzo: arriba, por la distancia a la llama; en el cuerpo, la misma cuenta que hacen los volúmenes.
        val cerca: (Offset) -> Float = { p ->
            if (p.y <= ch) alcance((p.x - llama.x) / w, (p.y - llama.y) / w, n) else cercaCuerpo((p.y - ch) / fila, n, p.x / w)
        }
        fun hacia(x: Float) = if (llama.x >= x) 1f else -1f
        /** Hacia dónde cae la sombra de algo en [p] sobre el muro: de día, a la derecha y abajo; de noche, lejos de la llama. */
        fun caida(p: Offset, largo: Float): Offset {
            if (!oscuro) return Offset(largo * 1.15f, largo * 0.8f)
            val v = p - llama; val dd = v.getDistance().coerceAtLeast(1f)
            return v / dd * (largo * 1.9f)
        }
        /** La sombra en el muro de lo que está de pie sobre la cornisa: no baja de donde pisa. */
        fun dePie(p: Offset, largo: Float): Offset = caida(p, largo).let { Offset(it.x, minOf(it.y, largo * 0.4f)) }
        val apoyo = w * APOYO
        val x0 = d.izquierda.right; val x1 = d.derecha.left

        muroAndalus(a, suelo, cerca)
        ventanaAndalus(a)
        // El halo del candil: unas pocas manchas, no un velo.
        if (a.lumbre) {
            val f = if (oscuro) 1f else 0.5f
            mancha(llama, w * 0.8f, FILO_MUEBLE.copy(alpha = 0.13f * f))
            mancha(llama, w * 0.4f, FILO_MUEBLE.copy(alpha = 0.24f * f))
            mancha(llama, w * 0.16f, LLAMA.copy(alpha = 0.3f * f))
        }
        sueloAndalus(a, suelo, cerca)

        // ---- el fondo de cada casillero: estuco pintado de índigo apagado, encalado y gastado, con la luz que entra por la izquierda
        d.repisas.forEachIndexed { k, r ->
            clipRect(x0, r.top, x1, r.bottom) {
                val an = x1 - x0
                fondoCasilleroAndalus(Rect(x0, r.top, x1, r.bottom), k, a, cerca)
                drawRect(a.sombra(if (oscuro) 0.2f else 0.12f), Offset(x0, r.top), Size(an, r.height))
                val f = if (oscuro) 1.2f else 1f
                drawRect(a.sombra(0.34f * f), Offset(x0, r.top), Size(an, 8f * u))
                drawRect(a.sombra(0.2f * f), Offset(x0, r.top), Size(an, 18f * u))
                drawRect(a.sombra(0.1f * f), Offset(x0, r.top), Size(an, 32f * u))
                drawRect(a.sombra(0.26f), Offset(x0, r.top), Size(9f * u, r.height))
                if (!oscuro) {
                    pinceladas(Rect(x0 + 9f * u, r.top + r.height * 0.3f, x0 + an * 0.6f, r.bottom), luzMueble(1f), 1755 + k, 14, u, if (noche) 0.04f else 0.07f)
                }
                pinceladas(Rect(x1 - an * 0.34f, r.top, x1, r.bottom), a.sombra(1f), 1763 + k, 20, u, 0.07f)
                drawRect(a.sombra(0.12f), Offset(x1 - 12f * u, r.top), Size(12f * u, r.height))
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

        // ---- las tablas, las pilastras y lo que se deja en los huecos libres
        d.tablas.forEachIndexed { k, r ->
            tablaAndalus(Rect(x0 - 1f * u, r.top, x1 + 1f * u, r.bottom), 3.5f * u, 1770 + 7 * k, a, d.placas.firstOrNull { p -> p.r.top >= r.top - 1f && p.r.bottom <= r.bottom + 1f }?.r) { x -> cerca(Offset(x, r.center.y)) }
        }
        listOf(d.izquierda, d.derecha).forEachIndexed { i, r -> pilastraAndalus(r, 1790 + i, a, n, ch, fila, hacia(r.center.x)) }
        d.repisas.forEachIndexed { k, r ->
            val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
            huecoLibreAndalus(k, r, oc, a, cerca(Offset((oc + r.right) / 2, r.center.y)), hacia((oc + r.right) / 2))
        }

        // ---- el rótulo de cada mes: una cartela lobulada de pergamino, clavada con dos clavos de bronce
        d.placas.forEach { p -> cartelaAndalus(p, medidor, a, cerca(p.r.center)) }

        // ---- el coronamiento: el friso de ataurique, los canecillos de rollos y la cornisa que vuela
        coronaAndalus(Rect(0f, apoyo, w, ch), a, cerca)

        // ---- el zócalo del mueble, con su arquería ciega
        zocaloAndalus(Rect(0f, b.top, w, suelo), a, cerca)

        // ---- lo que cuelga del muro y lo que hay sobre la cornisa, de atrás adelante
        val clavo = Offset(w * 0.448f, w * 0.085f)
        val cAstro = clavo + Offset(0f, w * 0.13f)
        astrolabioAndalus(clavo, w * 0.066f, a, cerca(cAstro), hacia(cAstro.x), caida(cAstro, w * 0.016f))
        val clavo2 = Offset(w * 0.6f, w * 0.06f)
        mistaraAndalus(clavo2, w * 0.085f, a, cerca(clavo2 + Offset(0f, w * 0.1f)), caida(clavo2 + Offset(0f, w * 0.1f), w * 0.012f))
        val pieLibros = Offset(w * 0.075f, apoyo)
        librosTumbadosAndalus(pieLibros, w * 0.215f, 3, a, cerca(pieLibros + Offset(w * 0.1f, -w * 0.03f)), 31, dePie(pieLibros + Offset(w * 0.1f, 0f), w * 0.012f))
        val pieRedoma = Offset(w * 0.235f, apoyo - w * 0.064f)
        redomaAndalus(pieRedoma, w * 0.1f, a, cerca(pieRedoma - Offset(0f, w * 0.04f)), hacia(pieRedoma.x), dePie(pieRedoma, w * 0.01f), alSol = true)
        // La luz de la celosía también toca lo que hay debajo: la cara de arriba de los códices y el canto de la cornisa.
        tramaAndalus(Rect(w * 0.085f, apoyo - w * 0.068f, w * 0.2f, apoyo - w * 0.061f), a, if (a.lumbre) 0.4f else 0.6f, 0.35f, 0.6f, 0.02f, blando = 0.4f)
        tramaAndalus(Rect(w * 0.1f, apoyo + w * 0.002f, w * 0.31f, apoyo + w * 0.016f), a, if (a.lumbre) 0.34f else 0.5f, 0.5f, 0.7f, 0.024f, blando = 0.5f)
        val pieCandil = Offset(w * 0.552f, apoyo)
        candilAndalus(pieCandil, w * 0.14f, a, cerca(pieCandil - Offset(0f, w * 0.04f)), dePie(pieCandil, w * 0.012f))
        val pieLaud = Offset(w * 0.86f, apoyo)
        laudAndalus(pieLaud, w * 0.43f, -17f, a, cerca(pieLaud - Offset(w * 0.04f, w * 0.12f)), hacia(pieLaud.x), dePie(pieLaud - Offset(0f, w * 0.1f), w * 0.016f))

        // ---- al pie: el códice abierto sobre su cojín de seda, la alcarraza en su posajarra y el arcón con su paño, cortado por el borde
        val pieCojin = Offset(w * 0.19f, h - (h - suelo) * 0.12f)
        cojinCodiceAndalus(pieCojin, w * 0.27f, a, maxOf(cerca(pieCojin), 0.16f), hacia(pieCojin.x))
        // La luz de la celosía cae también en el libro abierto y en la panza del cojín.
        tramaAndalus(Rect(pieCojin.x - w * 0.11f, pieCojin.y - w * 0.098f, pieCojin.x + w * 0.04f, pieCojin.y - w * 0.05f), a, if (a.oscuro) 0.2f else if (a.lumbre) 0.26f else 0.4f, 0.55f, 1.1f, 0.045f, blando = 0.6f)
        val pieArcon = Offset(w * 0.79f, h - (h - suelo) * 0.3f)
        arconAndalus(pieArcon, w * 0.26f, w * 0.12f, a, maxOf(cerca(pieArcon), 0.14f))
        val pieJarra = Offset(w * 0.6f, h - (h - suelo) * 0.16f)
        alcarrazaAndalus(pieJarra, w * 0.17f, a, maxOf(cerca(pieJarra), 0.18f), hacia(pieJarra.x))

        // ---- la llama, encima de todo, con su propio color
        if (a.lumbre) llamaCandilAndalus(llama, w * 0.03f)
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    // ---------------------------------------------------------------- el mueble

    /**
     * Una tabla de cedro: su cara de arriba, vista apenas ([fondo]), y el canto con su veta y su hilera de florecillas de cuatro
     * pétalos talladas (salvo donde va la cartela, [libre]). De noche se pinta por tramos, cada uno con la luz que le llega.
     */
    private fun DrawScope.tablaAndalus(cara: Rect, fondo: Float, semilla: Int, a: AireAndalus, libre: Rect?, cerca: (Float) -> Float) {
        val u = density
        val tramos = if (a.lumbre) 28 else 1
        for (t in 0 until tramos) {
            val xa = cara.left + cara.width * t / tramos; val xb = cara.left + cara.width * (t + 1) / tramos + 0.6f
            val k = cerca((xa + xb) / 2)
            drawRect(a.luz(mezcla(CEDRO, LUZ_MUEBLE, 0.28f), k), Offset(xa, cara.top - fondo), Size(xb - xa, fondo))
            drawRect(a.luz(CEDRO, k), Offset(xa, cara.top), Size(xb - xa, cara.height))
            if (a.oscuro) drawRect(FILO_MUEBLE.copy(alpha = 0.08f + 0.55f * k), Offset(xa, cara.top), Size(xb - xa, 1.1f * u))
        }
        clipRect(cara.left, cara.top - fondo, cara.right, cara.bottom) {
            vetas(cara, CEDRO_OSC.copy(alpha = if (a.oscuro) 0.5f else 0.4f), semilla, densidad = 0.6f, grosor = 0.8f, ondulacion = 0.7f)
            // La moldura: un filete arriba y una media caña abajo.
            drawRect(a.sombra(0.3f), Offset(cara.left, cara.top + cara.height * 0.2f), Size(cara.width, 0.8f * u))
            drawRect(a.claro(0.25f), Offset(cara.left, cara.top + cara.height * 0.2f + 0.8f * u), Size(cara.width, 0.6f * u))
            drawRect(a.sombra(0.2f), Offset(cara.left, cara.bottom - cara.height * 0.2f), Size(cara.width, cara.height * 0.2f))
            // Las florecillas talladas, de trecho en trecho.
            val paso = maxOf(cara.height * 1.6f, 8f * u)
            val rf = cara.height * 0.17f
            val yc = cara.top + cara.height * 0.52f
            var x = cara.left + paso * 0.6f
            val sombras = Path(); val caras = Path()
            while (x < cara.right - paso * 0.3f) {
                if (libre == null || x < libre.left - rf * 2f || x > libre.right + rf * 2f) {
                    for (q in 0 until 4) {
                        val ang = Math.toRadians(45.0 + 90.0 * q)
                        val o = Offset(x + rf * 0.7f * kotlin.math.cos(ang).toFloat(), yc + rf * 0.7f * kotlin.math.sin(ang).toFloat())
                        caras.addOval(Rect(o.x - rf * 0.55f, o.y - rf * 0.55f, o.x + rf * 0.55f, o.y + rf * 0.55f))
                        sombras.addOval(Rect(o.x - rf * 0.45f + 0.6f * u, o.y - rf * 0.45f + 0.7f * u, o.x + rf * 0.45f + 0.6f * u, o.y + rf * 0.45f + 0.7f * u))
                    }
                }
                x += paso
            }
            drawPath(caras, a.luz(mezcla(CEDRO, CEDRO_LUZ, 0.55f), cerca(cara.center.x) * 0.8f).copy(alpha = 0.75f))
            drawPath(sombras, a.sombra(0.32f))
        }
        if (!a.oscuro) drawRect(a.claro(0.45f), cara.topLeft, Size(cara.width, 1.1f * u))
        drawLine(a.sombra(0.4f), Offset(cara.left, cara.top - fondo), Offset(cara.right, cara.top - fondo), 0.7f * u)
        drawRect(a.sombra(0.4f), Offset(cara.left, cara.bottom - 1.6f * u), Size(cara.width, 1.6f * u))
    }

    /**
     * Una pilastra de cedro: la cara con su tablero rehundido y, dentro, un tallo de ataurique que sube ondulando y echa una
     * hoja en cada vuelta; la arista de la luz y el lado de la sombra. De noche se apaga hacia abajo, repisa a repisa.
     */
    private fun DrawScope.pilastraAndalus(r: Rect, semilla: Int, a: AireAndalus, repisas: Int, ch: Float, fila: Float, haciaX: Float) {
        val u = density
        if (r.height <= 1f || r.width <= 1f) return
        val fx = r.center.x / size.width
        val tramos = if (a.lumbre) (r.height / (fila * 0.25f)).toInt().coerceIn(4, 80) else 1
        fun k(y: Float) = cercaCuerpo((y - ch) / fila, repisas, fx)
        for (t in 0 until tramos) {
            val ya = r.top + r.height * t / tramos; val yb = r.top + r.height * (t + 1) / tramos + 0.6f
            drawRect(a.luz(CEDRO, k((ya + yb) / 2)), Offset(r.left, ya), Size(r.width, yb - ya))
        }
        val panel = Rect(r.left + r.width * 0.17f, r.top + 6f * u, r.right - r.width * 0.17f, r.bottom - 6f * u)
        clipRect(r.left, r.top, r.right, r.bottom) {
            vetas(r, CEDRO_OSC.copy(alpha = if (a.oscuro) 0.5f else 0.4f), semilla, densidad = 0.55f, vertical = true, grosor = 0.8f, ondulacion = 0.8f)
            // El tablero rehundido: más hondo, con la sombra arriba y a la izquierda (el canto le tapa la luz).
            for (t in 0 until tramos) {
                val ya = panel.top + panel.height * t / tramos; val yb = panel.top + panel.height * (t + 1) / tramos + 0.6f
                drawRect(a.luz(mezcla(ALMAGRA, CEDRO_OSC, 0.5f), k((ya + yb) / 2)), Offset(panel.left, ya), Size(panel.width, yb - ya))
            }
            drawRect(a.sombra(0.12f), panel.topLeft, panel.size)
            drawRect(a.sombra(0.25f), panel.topLeft, Size(1.4f * u, panel.height))
            drawRect(a.claro(0.3f), Offset(panel.right - 1f * u, panel.top), Size(1f * u, panel.height))
            // El tallo de ataurique.
            val tallo = Path(); val hojas = Path()
            val amp = panel.width * 0.26f
            val onda = maxOf(panel.width * 2.2f, 10f * u)
            var y = panel.bottom
            tallo.moveTo(panel.center.x, y)
            var i = 0
            while (y > panel.top && i < 400) {
                val l = if (i % 2 == 0) 1f else -1f
                val y2 = y - onda / 2
                tallo.cubicTo(panel.center.x + l * amp * 1.3f, y - onda * 0.12f, panel.center.x + l * amp * 1.3f, y2 + onda * 0.12f, panel.center.x, y2)
                // La hoja: una palmeta que se abre hacia el lado de la vuelta.
                val base = Offset(panel.center.x + l * amp * 0.9f, y - onda * 0.25f)
                // Una palmeta de tres lóbulos que se abre hacia el lado de la vuelta.
                hojas.moveTo(base.x, base.y)
                hojas.cubicTo(base.x + l * amp * 0.9f, base.y - amp * 0.1f, base.x + l * amp * 1.1f, base.y - amp * 0.9f, base.x + l * amp * 0.5f, base.y - amp * 1.2f)
                hojas.cubicTo(base.x + l * amp * 0.5f, base.y - amp * 0.8f, base.x + l * amp * 0.1f, base.y - amp * 0.9f, base.x - l * amp * 0.1f, base.y - amp * 1.6f)
                hojas.cubicTo(base.x - l * amp * 0.45f, base.y - amp * 1.0f, base.x - l * amp * 0.35f, base.y - amp * 0.4f, base.x, base.y)
                hojas.close()
                y = y2; i++
            }
            clipRect(panel.left, panel.top, panel.right, panel.bottom) {
                val cara = a.luz(mezcla(CEDRO, CEDRO_LUZ, 0.55f), k(r.top + fila * 0.5f) * 0.6f)
                drawPath(corrido(tallo, 1.1f * u, 1.1f * u), a.sombra(0.55f), style = Stroke(2.8f * u))
                drawPath(corrido(hojas, 1.1f * u, 1.1f * u), a.sombra(0.55f))
                drawPath(tallo, cara, style = Stroke(2.5f * u))
                drawPath(hojas, cara)
                drawPath(corrido(hojas, -0.6f * u, -0.6f * u), a.claro(0.3f), style = Stroke(0.6f * u))
                drawPath(corrido(tallo, -0.7f * u, -0.7f * u), a.claro(0.35f), style = Stroke(0.7f * u))
            }
        }
        // El capitel (un dado con su moldura) bajo el friso y la basa sobre el zócalo.
        val cap = Rect(r.left - 1f * u, r.top, r.right + 1f * u, r.top + 6f * u)
        drawRect(a.luz(mezcla(CEDRO, CEDRO_LUZ, 0.3f), k(r.top)), cap.topLeft, cap.size)
        drawRect(a.sombra(0.35f), Offset(cap.left, cap.bottom - 1.2f * u), Size(cap.width, 1.2f * u))
        val basa = Rect(r.left - 1f * u, r.bottom - 6f * u, r.right + 1f * u, r.bottom)
        drawRect(a.luz(mezcla(CEDRO, CEDRO_LUZ, 0.3f), k(r.bottom)), basa.topLeft, basa.size)
        drawRect(a.claro(0.3f), basa.topLeft, Size(basa.width, 1f * u))
        drawRect(a.claro(0.28f), r.topLeft, Size(r.width * 0.14f, r.height))
        drawRect(a.sombra(0.3f), Offset(r.right - r.width * 0.16f, r.top), Size(r.width * 0.16f, r.height))
        if (a.oscuro) {
            val x = if (haciaX > 0) r.right - 1.2f * u else r.left
            drawRect(FILO_MUEBLE.copy(alpha = 0.5f * k(r.top + fila * 0.2f)), Offset(x, r.top), Size(1.2f * u, fila * 0.9f))
        }
    }

    /**
     * El coronamiento, de [r].top (donde pisa lo de arriba) a [r].bottom (la primera repisa): la cornisa que vuela, con su cara
     * de arriba y su canto; debajo, los canecillos de rollos en hilera, y el friso de ataurique entre dos molduras.
     */
    private fun DrawScope.coronaAndalus(r: Rect, a: AireAndalus, cerca: (Offset) -> Float) {
        val u = density; val w = size.width
        val gC = w * 0.03f                 // el canto de la cornisa
        val fC = w * 0.013f                // lo que se ve de su cara de arriba
        val yCan = r.top + gC              // donde empiezan los canecillos
        val altoCan = w * 0.046f
        val friso = Rect(0f, yCan + altoCan, w, r.bottom)
        val tramos = if (a.lumbre) 24 else 1
        // El friso: el fondo hondo, el tallo que corre y las dos molduras.
        for (t in 0 until tramos) {
            val xa = w * t / tramos; val xb = w * (t + 1) / tramos + 0.6f
            val k = cerca(Offset((xa + xb) / 2, friso.center.y))
            drawRect(a.luz(mezcla(CEDRO, CEDRO_OSC, 0.35f), k), Offset(xa, yCan), Size(xb - xa, r.bottom - yCan))
        }
        val campo = Rect(0f, friso.top + 4f * u, w, friso.bottom - 5f * u)
        // El fondo del friso, pintado de almagra entre la talla (como las maderas califales).
        for (t in 0 until tramos) {
            val xa = w * t / tramos; val xb = w * (t + 1) / tramos + 0.6f
            drawRect(a.luz(mezcla(ALMAGRA, CEDRO_OSC, 0.45f), cerca(Offset((xa + xb) / 2, campo.center.y))), Offset(xa, campo.top), Size(xb - xa, campo.height))
        }
        frisoAndalus(campo, a, cerca(Offset(w * 0.5f, campo.center.y)))
        for (y in listOf(friso.top, friso.bottom - 5f * u)) {
            for (t in 0 until tramos) {
                val xa = w * t / tramos; val xb = w * (t + 1) / tramos + 0.6f
                drawRect(a.luz(mezcla(CEDRO, CEDRO_LUZ, 0.25f), cerca(Offset((xa + xb) / 2, y))), Offset(xa, y), Size(xb - xa, 4f * u))
            }
            drawRect(a.claro(0.35f), Offset(0f, y), Size(w, 0.9f * u))
            drawRect(a.sombra(0.35f), Offset(0f, y + 3f * u), Size(w, 1f * u))
        }
        // Los canecillos: el hueco entre ellos, hondo, y cada uno con sus tres rollos.
        drawRect(a.sombra(if (a.oscuro) 0.5f else 0.3f), Offset(0f, yCan), Size(w, altoCan))
        val paso = w * 0.09f
        var x = paso * 0.45f
        var i = 0
        while (x < w + paso && i < 40) {
            canecilloAndalus(Offset(x, yCan), w * 0.05f, altoCan, a, cerca(Offset(x, yCan + altoCan / 2)), 1960 + i)
            x += paso; i++
        }
        // La cornisa: la cara de arriba, el canto con su filete y su sombra en los canecillos.
        for (t in 0 until tramos) {
            val xa = -2f * u + (w + 4f * u) * t / tramos; val xb = -2f * u + (w + 4f * u) * (t + 1) / tramos + 0.6f
            val k = cerca(Offset((xa + xb) / 2, r.top))
            drawRect(a.luz(mezcla(CEDRO, LUZ_MUEBLE, 0.3f), k), Offset(xa, r.top - fC), Size(xb - xa, fC))
            drawRect(a.luz(CEDRO, k), Offset(xa, r.top), Size(xb - xa, gC))
            if (a.oscuro) drawRect(FILO_MUEBLE.copy(alpha = 0.08f + 0.6f * k), Offset(xa, r.top), Size(xb - xa, 1.2f * u))
        }
        val canto = Rect(-2f * u, r.top, w + 2f * u, r.top + gC)
        clipRect(canto.left, r.top - fC, canto.right, canto.bottom) {
            vetas(canto, CEDRO_OSC.copy(alpha = if (a.oscuro) 0.5f else 0.42f), 1951, densidad = 0.6f, grosor = 0.8f, ondulacion = 0.7f)
            vetas(Rect(canto.left, r.top - fC, canto.right, r.top), CEDRO_OSC.copy(alpha = 0.2f), 1952, densidad = 0.9f, grosor = 0.5f, ondulacion = 0.3f)
        }
        drawRect(a.sombra(0.35f), Offset(0f, r.top + gC * 0.55f), Size(w, 0.9f * u))
        drawRect(a.claro(0.3f), Offset(0f, r.top + gC * 0.55f + 0.9f * u), Size(w, 0.7f * u))
        if (!a.oscuro) drawRect(a.claro(0.5f), Offset(0f, r.top), Size(w, 1.1f * u))
        drawLine(a.sombra(0.4f), Offset(0f, r.top - fC), Offset(w, r.top - fC), 0.7f * u)
        drawRect(a.sombra(0.5f), Offset(0f, canto.bottom), Size(w, 3f * u))
        // El sol de la celosía sobre la cara de arriba de la cornisa, al pie de la ventana.
        tramaAndalus(Rect(w * 0.1f, r.top - fC, w * 0.36f, r.top), a, if (a.lumbre) 0.4f else 0.45f, 0.2f, 1.6f)
        // Bajo el friso, la sombra sobre la primera repisa.
        drawRect(a.sombra(0.4f), Offset(0f, r.bottom), Size(w, 2.5f * u))
    }

    /**
     * Un canecillo de rollos (el modillón califal), visto de frente y un poco desde la izquierda: tres rollos que menguan hacia
     * abajo, cada uno con su luz arriba y su sombra abajo, y a la derecha sus cabezas redondas en el costado; deja su sombra en
     * el friso, abajo a la derecha.
     */
    private fun DrawScope.canecilloAndalus(arriba: Offset, an: Float, alto: Float, a: AireAndalus, cerca: Float, semilla: Int) {
        val u = density
        val cara = a.luz(CEDRO, cerca)
        val costado = a.luz(mezcla(CEDRO, CEDRO_OSC, 0.45f), cerca)
        val alt = alto / 3f
        drawPath(poligono(arriba.x - an * 0.4f, arriba.y + 1f * u, arriba.x + an * 0.75f, arriba.y + 1f * u, arriba.x + an * 0.85f, arriba.y + alto + 4f * u, arriba.x - an * 0.2f, arriba.y + alto + 4f * u), a.sombra(if (a.oscuro) 0.5f else 0.34f))
        for (j in 0 until 3) {
            val f = 1f - 0.15f * j
            val rr = Rect(arriba.x - an * 0.5f * f, arriba.y + alt * j, arriba.x + an * 0.5f * f, arriba.y + alt * (j + 1))
            // La cabeza del rollo en el costado: medio disco que asoma a la derecha, con su espiral.
            val cab = Rect(rr.right - alt * 0.5f, rr.top, rr.right + alt * 0.5f, rr.bottom)
            drawOval(costado, cab.topLeft, cab.size)
            drawArc(a.sombra(0.4f), -60f, 200f, false, cab.topLeft + Offset(alt * 0.22f, alt * 0.22f), Size(alt * 0.56f, alt * 0.56f), style = Stroke(0.6f * u))
            drawRect(cara, rr.topLeft, rr.size)
            vetas(rr, CEDRO_OSC.copy(alpha = 0.22f), semilla + j, densidad = 0.5f, grosor = 0.5f)
            drawRect(a.claro(0.42f), Offset(rr.left, rr.top + alt * 0.1f), Size(rr.width, alt * 0.24f))
            drawRect(a.sombra(0.36f), Offset(rr.left, rr.bottom - alt * 0.32f), Size(rr.width, alt * 0.32f))
            drawLine(a.sombra(0.55f), Offset(rr.left, rr.bottom), Offset(cab.right, rr.bottom), 0.8f * u)
        }
        if (a.oscuro) drawRect(FILO_MUEBLE.copy(alpha = 0.5f * cerca), Offset(arriba.x - an * 0.5f, arriba.y), Size(an, 1f * u))
    }

    /**
     * El ataurique del friso, tallado en relieve: dos tallos que ondulan a contrapaso y se cruzan, dejando una hilera de
     * almendras; en cada una, alternas, una piña con su escamado o una flor de cuatro pétalos, y en cada cruce dos medias
     * palmetas que llenan los huecos de arriba y de abajo.
     */
    private fun DrawScope.frisoAndalus(r: Rect, a: AireAndalus, cerca: Float) {
        val u = density
        if (r.width <= 1f || r.height <= 1f) return
        val onda = maxOf(r.height * 2.8f, 14f * u)
        val amp = r.height * 0.36f
        val cy = r.center.y
        val tallos = Path(); val hojas = Path(); val escamas = Path()
        var x = r.left - onda * 0.25f
        var i = 0
        while (x < r.right + onda && i < 200) {
            val x2 = x + onda / 2
            for (l in listOf(-1f, 1f)) {
                val s = if (i % 2 == 0) l else -l
                tallos.moveTo(x, cy)
                tallos.cubicTo(x + onda / 6f, cy + s * amp * 1.33f, x2 - onda / 6f, cy + s * amp * 1.33f, x2, cy)
                // La media palmeta del cruce, hacia arriba y hacia abajo, que se abre hacia los dos lados.
                for (q in listOf(-1f, 1f)) {
                    val p0 = Offset(x, cy + l * amp * 0.35f)
                    hojas.moveTo(p0.x, p0.y)
                    hojas.cubicTo(p0.x + q * onda * 0.05f, p0.y + l * amp * 0.5f, p0.x + q * onda * 0.16f, p0.y + l * amp * 0.75f, p0.x + q * onda * 0.2f, p0.y + l * amp * 1.05f)
                    hojas.cubicTo(p0.x + q * onda * 0.1f, p0.y + l * amp * 0.85f, p0.x + q * onda * 0.02f, p0.y + l * amp * 0.75f, p0.x, p0.y + l * amp * 0.9f)
                    hojas.close()
                }
            }
            // Lo de dentro de la almendra.
            val c = Offset(x + onda / 4f, cy)
            if (i % 2 == 0) {
                hojas.addOval(Rect(c.x - onda * 0.1f, c.y - amp * 0.62f, c.x + onda * 0.1f, c.y + amp * 0.62f))
                for (q in -1..1) {
                    escamas.moveTo(c.x - onda * 0.1f, c.y + q * amp * 0.34f - amp * 0.2f); escamas.lineTo(c.x + onda * 0.1f, c.y + q * amp * 0.34f + amp * 0.2f)
                    escamas.moveTo(c.x + onda * 0.1f, c.y + q * amp * 0.34f - amp * 0.2f); escamas.lineTo(c.x - onda * 0.1f, c.y + q * amp * 0.34f + amp * 0.2f)
                }
            } else {
                for (q in 0 until 4) {
                    val ang = Math.toRadians(90.0 * q)
                    val o = Offset(c.x + amp * 0.32f * kotlin.math.cos(ang).toFloat(), c.y + amp * 0.32f * kotlin.math.sin(ang).toFloat())
                    hojas.addOval(Rect(o.x - amp * 0.26f, o.y - amp * 0.26f, o.x + amp * 0.26f, o.y + amp * 0.26f))
                }
            }
            x = x2; i++
        }
        val cara = a.luz(mezcla(CEDRO, CEDRO_LUZ, 0.55f), cerca)
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawPath(corrido(tallos, 1f * u, 1.1f * u), a.sombra(0.55f), style = Stroke(2.4f * u))
            drawPath(corrido(hojas, 1f * u, 1.1f * u), a.sombra(0.5f))
            drawPath(tallos, cara, style = Stroke(2.1f * u))
            drawPath(hojas, cara)
            drawPath(escamas, a.sombra(0.45f), style = Stroke(0.7f * u))
            drawPath(corrido(tallos, -0.6f * u, -0.7f * u), a.claro(0.4f), style = Stroke(0.7f * u))
        }
    }

    /**
     * El zócalo del mueble, de [r].top a [r].bottom (donde empieza el suelo): una moldura que vuela arriba, una arquería ciega
     * de arquillos de herradura tallada en el tablero y la moldura de abajo, que pisa el suelo.
     */
    private fun DrawScope.zocaloAndalus(r: Rect, a: AireAndalus, cerca: (Offset) -> Float) {
        val u = density; val w = size.width
        if (r.height <= 1f) return
        val tramos = if (a.lumbre) 20 else 1
        for (t in 0 until tramos) {
            val xa = w * t / tramos; val xb = w * (t + 1) / tramos + 0.6f
            drawRect(a.luz(CEDRO, cerca(Offset((xa + xb) / 2, r.center.y))), Offset(xa, r.top), Size(xb - xa, r.height))
        }
        vetas(r, CEDRO_OSC.copy(alpha = if (a.oscuro) 0.5f else 0.4f), 1971, densidad = 0.6f, grosor = 0.8f, ondulacion = 0.6f)
        val moldura = r.height * 0.16f
        drawRect(a.luz(mezcla(CEDRO, CEDRO_LUZ, 0.35f), cerca(Offset(w * 0.5f, r.top))), Offset(-2f * u, r.top), Size(w + 4f * u, moldura))
        drawRect(a.claro(0.4f), Offset(0f, r.top), Size(w, 1f * u))
        drawRect(a.sombra(0.45f), Offset(0f, r.top + moldura), Size(w, 2.4f * u))
        // La arquería ciega.
        val arcos = Rect(w * 0.03f, r.top + moldura + 4f * u, w * 0.97f, r.bottom - r.height * 0.18f)
        if (arcos.height > 4f * u) {
            val n = (arcos.width / (arcos.height * 0.78f)).toInt().coerceIn(3, 30)
            val an = arcos.width / n
            for (i in 0 until n) {
                val v = Rect(arcos.left + an * i + an * 0.16f, arcos.top + an * 0.08f, arcos.left + an * (i + 1) - an * 0.16f, arcos.bottom)
                val forma = arcoHerradura(v)
                // El fondo del arquillo, hondo y pintado de almagra; el arco lo ensombrece arriba y la luz le da en el borde de abajo.
                drawPath(forma, a.luz(mezcla(ALMAGRA, CEDRO_OSC, 0.5f), cerca(v.center)))
                drawPath(borde(forma, 1.6f * u, 2.4f * u), a.sombra(0.5f))
                drawPath(borde(forma, -1f * u, -1.1f * u), a.claro(0.32f))
                drawLine(a.claro(0.25f), Offset(v.left + 1f * u, v.bottom - 0.8f * u), Offset(v.right - 1f * u, v.bottom - 0.8f * u), 0.8f * u)
            }
        }
        // Las juntas de las tablas del zócalo, los clavos de la moldura y el uso: los roces de los pies abajo y la mugre en el canto.
        val juntas = Path()
        var jx = w * 0.21f
        while (jx < w) { juntas.moveTo(jx, r.top + moldura); juntas.lineTo(jx, r.bottom); jx += maxOf(w * 0.26f, 4f * u) }
        drawPath(juntas, a.sombra(0.4f), style = Stroke(0.8f * u))
        var cx = w * 0.06f
        while (cx < w) {
            drawCircle(a.sombra(0.4f), 1.3f * u, Offset(cx + 0.5f * u, r.top + moldura * 0.55f + 0.5f * u))
            drawCircle(a.luz(mezcla(BRONCE, CEDRO_OSC, 0.3f), cerca(Offset(cx, r.top))), 1.1f * u, Offset(cx, r.top + moldura * 0.55f))
            cx += maxOf(w * 0.13f, 4f * u)
        }
        for (q in 0 until 7) {
            val o = Offset(w * (0.05f + 0.9f * azar(1975, q)), r.bottom - r.height * (0.06f + 0.1f * azar(1976, q)))
            drawOval(a.claro(0.22f), Offset(o.x - w * 0.03f, o.y - 1.2f * u), Size(w * (0.03f + 0.04f * azar(1977, q)), 2.4f * u))
        }
        drawRect(a.sombra(0.3f), Offset(0f, r.bottom - 2.2f * u), Size(w, 2.2f * u))
        drawRect(a.luz(mezcla(CEDRO, CEDRO_LUZ, 0.2f), cerca(Offset(w * 0.5f, r.bottom))), Offset(-2f * u, r.bottom - r.height * 0.14f), Size(w + 4f * u, r.height * 0.14f))
        drawRect(a.claro(0.3f), Offset(0f, r.bottom - r.height * 0.14f), Size(w, 0.9f * u))
        drawRect(a.claro(0.24f), Offset(0f, r.top), Size(w * 0.012f, r.height))
    }

    /** La cartela lobulada de pergamino del rótulo de un mes, clavada en el canto de la tabla. */
    private fun DrawScope.cartelaAndalus(p: PlacaEstante, medidor: TextMeasurer, a: AireAndalus, cerca: Float) {
        val u = density
        val r = Rect(p.r.left, p.r.top - 1f * u, p.r.right, p.r.bottom + 1f * u)
        val lob = r.height * 0.5f
        val forma = Path().apply {
            addRoundRect(androidx.compose.ui.geometry.RoundRect(r.left + lob * 0.6f, r.top, r.right - lob * 0.6f, r.bottom, CornerRadius(1.2f * u)))
            addOval(Rect(r.left, r.center.y - lob * 0.78f, r.left + lob * 1.56f, r.center.y + lob * 0.78f))
            addOval(Rect(r.right - lob * 1.56f, r.center.y - lob * 0.78f, r.right, r.center.y + lob * 0.78f))
        }
        drawPath(corrido(forma, 1.3f * u, 1.7f * u), a.sombra(0.4f))
        drawPath(forma, a.etiqueta(maxOf(cerca, 0.15f)))
        clipPath(forma) {
            pinceladas(r, a.sombra(1f), 1981 + p.texto.length, 8, u, 0.05f)
            drawRect(a.sombra(0.12f), Offset(r.left, r.bottom - 2f * u), Size(r.width, 2f * u))
        }
        val filete = if (a.oscuro) mezcla(ALMAGRA, a.hondo, 0.25f) else ALMAGRA
        drawPath(forma, filete, style = Stroke(0.8f * u))
        listOf(r.left + lob * 0.78f, r.right - lob * 0.78f).forEach { x ->
            drawCircle(a.sombra(0.45f), 1.6f * u, Offset(x + 0.5f * u, r.center.y + 0.6f * u))
            drawCircle(a.luz(BRONCE, maxOf(cerca, 0.3f)), 1.4f * u, Offset(x, r.center.y))
            drawCircle(a.claro(0.5f), 0.5f * u, Offset(x - 0.4f * u, r.center.y - 0.4f * u))
        }
        textoEn(medidor, p.texto, Rect(r.left + lob * 1.6f, r.top + 1.5f * u, r.right - lob * 1.6f, r.bottom - 1.5f * u), LETRA, a.tinta, 11f, peso = FontWeight.Medium, espaciado = 0.5f, minimo = 5f)
    }

    // ---------------------------------------------------------------- cada entrada

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val a = AireAndalus(oscuro, v.noche)
        val k = if (a.lumbre) cercaCuerpo(v.repisa + 0.55f, v.repisas, LADO + v.centro * (1f - 2 * LADO)) else 1f
        when (clase(v.semilla)) {
            0 -> codiceAndalus(v, medidor, a, k)
            1 -> estucheAndalus(v, medidor, a, k)
            else -> legajoAndalus(v, medidor, a, k)
        }
    }
}
