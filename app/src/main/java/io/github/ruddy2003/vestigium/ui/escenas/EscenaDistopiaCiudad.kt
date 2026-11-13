package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDistopia.Torre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cable
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.haloDeBruma
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hojaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoMojado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * La capital, detrás de la plaza: la lejanía con sus chimeneas, el ministerio y la torre nueva, y la avenida que huye
 * hacia ellos entre dos filas de bloques. Todo lo que está sobre la avenida se coloca con una profundidad `u`: 1 es el
 * borde de la plaza y 0, el horizonte; a esa profundidad el suelo queda en [gy] y las cosas miden `u` veces su tamaño.
 */
internal object CiudadDistopia {
    private val C = ColorDistopia
    private const val H = EscenaDistopia.H
    private const val VX = EscenaDistopia.VX

    /** El borde de la plaza: donde empieza la avenida. */
    const val BORDE = 206f

    /** La altura del suelo de la avenida a la profundidad [u]. */
    fun gy(u: Float): Float = H + (BORDE - H) * u

    /** Los bordillos de la avenida y las líneas de fachada de cada acera, a la profundidad [u]. */
    fun bordilloIzq(u: Float) = VX - 86f * u
    fun bordilloDer(u: Float) = VX + 94f * u
    private fun fachadaIzq(u: Float) = VX - 110f * u
    private fun fachadaDer(u: Float) = VX + 140f * u

    /** Cuánta bruma tiene delante algo que está a la profundidad [u]. */
    private fun lejosDe(u: Float) = (0.5f - 0.6f * u).coerceIn(0.04f, 0.5f)

    /** El color de una cara plana: [hacia] va de −1 (da la espalda a la luz) a 1 (la mira de lleno). */
    internal fun cara(l: Luces, r: Rayo, propio: Color, hacia: Float, lejos: Float = 0f): Color {
        val c = if (hacia >= 0f) lerp(propio, C.LUZ, 0.3f * hacia * r.contraste) else mult(propio, r.color, 0.9f * -hacia * r.contraste)
        return l.tono(c, lejos)
    }

    /** Hacia dónde mira, respecto de la luz, una cara que da a la derecha (+1) o a la izquierda (−1). */
    internal fun lado(r: Rayo, mira: Float) = (r.lat * mira).coerceIn(-1f, 1f) * (0.5f + 0.5f * r.dia) - (1f - r.dia) * 0.25f

    /** Una cara que mira a quien mira el cuadro. */
    internal fun frente(r: Rayo) = 0.12f * r.dia - (1f - r.dia) * 0.2f

    /** Un bloque de viviendas de la avenida: de qué lado está, de [u0] (cerca) a [u1] (lejos), su altura a `u` = 1, sus pisos y sus ventanas por piso. */
    private class Bloque(val lado: Float, val u0: Float, val u1: Float, val alto: Float, val pisos: Int, val cols: Int, val fondo: Float, val n: Int, val ciego: Boolean = false) {
        fun fx(u: Float) = if (lado < 0f) fachadaIzq(u) else fachadaDer(u)
        fun cima(u: Float) = gy(u) - alto * u
    }

    private val BLOQUES = listOf(
        Bloque(-1f, 0.92f, 0.63f, 206f, 12, 8, 70f, 0, ciego = true),
        Bloque(-1f, 0.52f, 0.39f, 206f, 12, 6, 70f, 1),
        Bloque(-1f, 0.34f, 0.25f, 206f, 12, 5, 70f, 2),
        Bloque(-1f, 0.21f, 0.15f, 190f, 11, 4, 70f, 3),
        Bloque(-1f, 0.125f, 0.085f, 206f, 12, 3, 70f, 4),
        Bloque(1f, 0.50f, 0.37f, 140f, 8, 6, 70f, 5),
        Bloque(1f, 0.30f, 0.24f, 150f, 9, 5, 70f, 6),
        Bloque(1f, 0.20f, 0.14f, 140f, 8, 4, 70f, 7),
    )

    /** Las farolas de la avenida: (profundidad, a la derecha). La primera de la derecha es la de la plaza. */
    private val FAROLAS = listOf(0.808f to true, 0.62f to false, 0.5f to true, 0.4f to false, 0.33f to true, 0.27f to false, 0.22f to true, 0.18f to false, 0.15f to true, 0.12f to false, 0.10f to true)

    /** Las bocacalles: (lado, de qué profundidad a cuál). Por ellas entra el sol cuando los bloques dan sombra a la avenida. */
    private val BOCACALLES = listOf(Triple(-1f, 0.63f, 0.52f), Triple(1f, 0.37f, 0.30f))

    /** Las dos vías del tranvía: a qué distancia del eje va cada una. Acaban en la plaza, en sus topes. */
    private val VIAS = listOf(-45f, 53f)
    private const val MEDIA_VIA = 5f
    private const val FIN_DE_VIA = 0.86f

    /** La altura del hilo de contacto sobre la calzada (a `u` = 1) y dónde se para el tranvía. */
    private const val HILO = 34f
    private const val TRANVIA_U = 0.66f

    /** Un punto de la calzada: a [X] del eje, a la profundidad [u]. */
    private fun enVia(X: Float, u: Float) = Offset(VX + X * u, gy(u))
    private fun hilo(cx: Float, u: Float) = Offset(VX + cx * u, gy(u) - HILO * u)

    /** Cuánta bruma lleva el asfalto a la profundidad [u] (la misma del degradado de la calzada). */
    private fun lejosDelAsfalto(u: Float) = if (u < 0.5f) 0.6f - 0.6f * u else 0.3f - 0.48f * (u - 0.5f)

    private fun pieDeFarola(u: Float, derecha: Boolean) = Offset(if (derecha) bordilloDer(u) + 2f * u else bordilloIzq(u) - 2f * u, gy(u))

    /** Las chimeneas de la fábrica, a lo lejos: (x, cima, grueso). */
    private val CHIMENEAS = listOf(Triple(288f, 82f, 3.4f), Triple(331f, 76f, 4f), Triple(353f, 88f, 3f), Triple(118f, 96f, 2.4f))

    // ---------------------------------------------------------------- La parte quieta

    fun DrawScope.capital(m: Momento, l: Luces, r: Rayo) {
        val fijo = Azar(Semilla.de("distopia", "capital"))
        cieloDeLaCapital(m, l, r)
        lejania(m, l, r, fijo)
        if (EscenaDistopia.cohetes(m)) cohetesQuietos(l)
        torreNueva(m, l, r)
        ministerio(m, l, r)
        reflectores(m, l)
        bloquesDeEnfrente(m, l, r)
        suelo(m, l, r, fijo)
        capaSombra(r) { c -> sombrasDeLaAvenida(r, c) }
        charcosDeLuz(m, l)
        // Los bloques, las farolas y los árboles, de lejos a cerca.
        val cosas = ArrayList<Pair<Float, DrawScope.() -> Unit>>()
        for (b in BLOQUES) cosas += b.u0 to { bloque(m, l, r, b) }
        for ((u, der) in FAROLAS) cosas += u + 0.001f to { farola(m, l, r, u, der) }
        for ((i, u) in listOf(0.66f, 0.42f, 0.28f, 0.19f).withIndex()) cosas += u to { desmochado(m, l, r, Offset(VX + 106f * u, gy(u)), 80f * u, lejosDe(u), i) }
        for (cx in VIAS) cosas += FIN_DE_VIA + 0.004f to { tope(m, l, r, cx) }
        if (EscenaDistopia.tranvia(m)) cosas += TRANVIA_U to { tranvia(m, l, r) }
        when (EscenaDistopia.furgon(m)) { EscenaDistopia.Furgon.APARCADO -> cosas += 0.442f to { furgon(m, l, r, 0.44f, false) }; EscenaDistopia.Furgon.EN_EL_PORTAL -> cosas += 0.782f to { furgon(m, l, r, 0.78f, true) }; EscenaDistopia.Furgon.NINGUNO -> Unit }
        for ((_, dibuja) in cosas.sortedBy { it.first }) dibuja()
        catenaria(l)
        pantalla(m, l)
        if (EscenaDistopia.desfile(m) || EscenaDistopia.fiesta(m)) colgadoDeLaAvenida(m, l)
        caseta(m, l, r)
        vallas(m, l, r)
    }

    /**
     * Lo que tiene de suyo el cielo de la capital, y cambia cada día: las vetas del humo de las fábricas, tendidas por
     * el viento, y los globos cautivos de la defensa, que hoy están en un sitio y mañana en otro.
     */
    private fun DrawScope.cieloDeLaCapital(m: Momento, l: Luces, r: Rayo) {
        val az = m.azar("cielo de la capital")
        val humo = lerp(l.bruma, l.tono(C.HORMIGON_OSC, 0.5f), 0.4f)
        repeat(4 + az.entero(3)) { mancha(Offset(az.entre(120f, ANCHO + 20f), az.entre(62f, H - 8f)), az.entre(50f, 120f), az.entre(2.4f, 6f), humo, az.entre(0.25f, 0.5f)) }
        repeat(2) { mancha(Offset(az.entre(60f, ANCHO), az.entre(20f, 60f)), az.entre(70f, 140f), az.entre(2f, 4.4f), lerp(humo, l.alto, 0.5f), 0.3f) }
        // Los globos: de uno a tres, lejos, cada uno con su cable. No se ponen delante del sol de la mañana ni de la luna.
        val sitios = listOf(Offset(214f, 44f), Offset(262f, 26f), Offset(352f, 38f), Offset(232f, 70f), Offset(366f, 66f), Offset(196f, 22f)).let { todos -> val d = az.entero(todos.size); listOf(todos[d], todos[(d + 2) % todos.size], todos[(d + 3) % todos.size]) }
        for ((i, c0) in sitios.take(1 + az.entero(3)).withIndex()) {
            val c = c0 + Offset(az.entre(-8f, 8f), az.entre(-4f, 4f)); val k = 0.7f + 0.3f * ((i + az.entero(2)) % 2); val lejos = 0.62f - 0.12f * k
            drawLine(l.tono(C.HOLLIN, lejos).copy(alpha = 0.5f), c + Offset(-1f * k, 3f * k), Offset(c.x - 14f, H), 0.3f)
            val cuerpo = Path().apply { moveTo(c.x - 9f * k, c.y); cubicTo(c.x - 8f * k, c.y - 4.6f * k, c.x + 5f * k, c.y - 4.4f * k, c.x + 9f * k, c.y - 0.6f * k); cubicTo(c.x + 5f * k, c.y + 3.6f * k, c.x - 8f * k, c.y + 4f * k, c.x - 9f * k, c.y); close() }
            drawPath(cuerpo, Brush.verticalGradient(listOf(cara(l, r, C.HUESO, 0.8f, lejos), cara(l, r, C.HORMIGON, 0.1f, lejos), cara(l, r, C.HORMIGON_OSC, -0.8f, lejos)), c.y - 4.4f * k, c.y + 4f * k))
            for (s in listOf(-1f, 0f, 1f)) drawPath(poligono(c + Offset(6f * k, -0.4f * k), c + Offset(11.6f * k, (-3.6f + 3.4f * (s + 1f)) * k - 0.6f * k), c + Offset(10.6f * k, (-1.6f + 1.4f * (s + 1f)) * k)), cara(l, r, C.HORMIGON_OSC, -0.4f * s, lejos))
            if (l.noche > 0.4f && EscenaDistopia.corriente(m)) drawCircle(C.PILOTO.copy(alpha = 0.8f), 0.7f, c + Offset(0f, 4f * k))
        }
    }

    /** Lo más lejano: dos filas de bloques en la bruma, las chimeneas de la fábrica y el gasómetro. */
    private fun DrawScope.lejania(m: Momento, l: Luces, r: Rayo, az: Azar) {
        val encendidas = EscenaDistopia.ventanas(m)
        fun fila(lejos: Float, x0: Float, altoMin: Float, altoMax: Float, propio: Color, semilla: Int) {
            val f = Azar(Semilla.de("distopia", "fila", semilla))
            var x = x0
            while (x < ANCHO + 10f) {
                val w = f.entre(10f, 24f); val h = f.entre(altoMin, altoMax)
                val c = cara(l, r, propio, lado(r, 1f) * 0.6f, lejos); val s = cara(l, r, propio, lado(r, -1f) * 0.6f, lejos)
                drawRect(c, Offset(x, H + 2f - h), Size(w, h)); drawRect(s, Offset(if (r.lat >= 0f) x else x + w * 0.62f, H + 2f - h), Size(w * 0.38f, h))
                if (f.real() > 0.6f) drawRect(c, Offset(x + w * 0.3f, H + 2f - h - 2.2f), Size(w * 0.3f, 2.4f))
                // Las ventanas: de día, rayas; de noche, puntos de luz.
                var y = H + 2f - h + 2f
                while (y < H) { var xx = x + 1.4f; while (xx < x + w - 1.4f) { val t = f.real(); if (l.noche > 0.3f && t < encendidas) drawRect(Color(0xFFFFD48A).copy(alpha = 0.7f * (1f - lejos * 0.5f)), Offset(xx, y), Size(0.9f, 0.9f)); xx += 2.4f }; y += 2.6f }
                x += w + f.entre(-2f, 9f)
            }
        }
        fila(0.82f, 60f, 6f, 22f, Color(0xFFA6AAA0), 1)
        // Las chimeneas, anilladas de rojo y hueso, y el gasómetro.
        for ((x, cima, g) in CHIMENEAS) {
            val forma = poligono(Offset(x - g, H + 2f), Offset(x - g * 0.62f, cima), Offset(x + g * 0.62f, cima), Offset(x + g, H + 2f))
            drawPath(forma, Brush.horizontalGradient(if (r.lat >= 0f) listOf(cara(l, r, C.HORMIGON_OSC, -0.7f, 0.7f), cara(l, r, C.HORMIGON, 0.8f, 0.7f)) else listOf(cara(l, r, C.HORMIGON, 0.8f, 0.7f), cara(l, r, C.HORMIGON_OSC, -0.7f, 0.7f)), x - g, x + g))
            clipPath(forma) { for (i in 0..1) drawRect(l.tono(C.ROJO, 0.66f), Offset(x - g, cima + 2f + i * 5f), Size(g * 2f, 2.2f)) }
            if (l.noche > 0.4f && EscenaDistopia.corriente(m)) drawCircle(C.PILOTO.copy(alpha = 0.9f), 0.9f, Offset(x, cima - 0.6f))
        }
        val gas = Rect(308f, 104f, 326f, H + 2f)
        drawRoundRect(Brush.horizontalGradient(if (r.lat >= 0f) listOf(cara(l, r, C.VERDE_GRIS, -0.6f, 0.68f), cara(l, r, C.VERDE_GRIS, 0.7f, 0.68f)) else listOf(cara(l, r, C.VERDE_GRIS, 0.7f, 0.68f), cara(l, r, C.VERDE_GRIS, -0.6f, 0.68f)), gas.left, gas.right), gas.topLeft, gas.size, androidx.compose.ui.geometry.CornerRadius(3f))
        for (xx in listOf(308f, 314f, 320f, 326f)) drawLine(l.tono(C.HIERRO, 0.66f), Offset(xx, 100f), Offset(xx, H + 2f), 0.4f)
        drawLine(l.tono(C.HIERRO, 0.66f), Offset(307f, 100.4f), Offset(327f, 100.4f), 0.4f)
        fila(0.66f, 84f, 5f, 15f, Color(0xFFB0B2A4), 2)
        // La bruma del fondo de la avenida.
        mancha(Offset(VX, H + 1f), 150f, 9f, l.bruma, 0.5f)
    }

    /** El día de los cohetes: lo que no se mueve del barrio de los obreros, a la derecha, es el resplandor de los incendios. */
    private fun DrawScope.cohetesQuietos(l: Luces) {
        for ((x, w) in listOf(300f to 34f, 352f to 26f)) { mancha(Offset(x, H - 2f), w, 9f, Color(0xFFFF8A3A), 0.3f + 0.4f * l.noche); mancha(Offset(x, H - 10f), w * 1.3f, 16f, l.tono(C.HOLLIN, 0.5f), 0.4f) }
        // Las estelas de los que cayeron: dos arcos blancos que bajan del cielo.
        for ((a, b) in listOf(Offset(388f, 18f) to Offset(302f, H - 4f), Offset(250f, 6f) to Offset(350f, H - 4f)))
            drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x + (b.x - a.x) * 0.5f, a.y + 4f, b.x + (a.x - b.x) * 0.1f, b.y - 50f, b.x, b.y) }, lerp(l.bajo, Color.White, 0.6f).copy(alpha = 0.5f), style = Stroke(1.1f, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- El ministerio y la torre nueva

    /**
     * El emblema del Estado: un ojo, sin cara, dibujado **en línea** con el color [linea] sobre el campo que haya debajo
     * (nunca una mancha clara rellena: a tamaño pequeño un disco claro sobre rojo recordaría a una bandera real). Es una
     * almendra muy alargada con su párpado encima, el aro del iris (relleno de [iris]) y el punto de la pupila. [w]: su ancho.
     */
    internal fun DrawScope.ojo(c: Offset, w: Float, linea: Color, iris: Color, alfa: Float = 1f) {
        val h = w * 0.25f; val g = maxOf(0.45f, w * 0.07f); val tinta = linea.copy(alpha = alfa)
        val almendra = Path().apply {
            moveTo(c.x - w / 2f, c.y + h * 0.12f); cubicTo(c.x - w * 0.22f, c.y - h, c.x + w * 0.2f, c.y - h, c.x + w / 2f, c.y + h * 0.12f)
            cubicTo(c.x + w * 0.2f, c.y + h * 0.92f, c.x - w * 0.22f, c.y + h * 0.92f, c.x - w / 2f, c.y + h * 0.12f); close()
        }
        drawCircle(iris.copy(alpha = alfa), w * 0.115f, c)
        drawPath(almendra, tinta, style = Stroke(g, join = StrokeJoin.Round))
        drawPath(Path().apply { moveTo(c.x - w * 0.47f, c.y - h * 0.55f); cubicTo(c.x - w * 0.2f, c.y - h * 1.8f, c.x + w * 0.2f, c.y - h * 1.8f, c.x + w * 0.47f, c.y - h * 0.55f) }, tinta, style = Stroke(g * 0.8f, cap = StrokeCap.Round))
        drawCircle(tinta, w * 0.115f, c, style = Stroke(g * 0.85f))
        drawCircle(tinta, maxOf(0.3f, w * 0.045f), c)
    }

    /** Una fila de rendijas: las ventanas del ministerio. [encendidas], de 0 a 1. */
    private fun DrawScope.rendijas(l: Luces, zona: Rect, cada: Float, pisoAlto: Float, encendidas: Float, lejos: Float, az: Azar) {
        val hueco = l.tono(lerp(C.CRISTAL, C.HOLLIN, 0.4f), lejos)
        var y = zona.top
        while (y < zona.bottom - pisoAlto * 0.5f) {
            var x = zona.left
            while (x < zona.right - 0.4f) {
                val t = az.real()
                drawRect(if (t < encendidas) Color(0xFFFFE2A0).copy(alpha = 0.95f - 0.4f * lejos) else hueco.copy(alpha = 0.75f), Offset(x, y), Size(0.8f, pisoAlto * 0.62f))
                x += cada
            }
            y += pisoAlto
        }
    }

    /**
     * El ministerio, al final de la avenida: el fuste de hormigón blanco con sus nervios y sus rendijas, las dos alas, la
     * coronación escalonada con el ojo, y la bandera enorme de la azotea. En campaña y los días de desfile lleva
     * colgaduras; de noche lo alumbran desde abajo.
     */
    private fun DrawScope.ministerio(m: Momento, l: Luces, r: Rayo) {
        val lejos = 0.42f
        val az = Azar(Semilla.de("distopia", "ministerio"))
        val enc = EscenaDistopia.rendijas(m)
        val luz = cara(l, r, C.CAL, lado(r, 1f).coerceAtLeast(0.1f), lejos); val sombra = cara(l, r, C.CAL, lado(r, -1f).coerceAtMost(-0.3f), lejos); val medio = cara(l, r, C.CAL, frente(r), lejos)
        val solDer = r.lat >= 0f
        fun cuerpo(x0: Float, x1: Float, y0: Float, y1: Float, canto: Float = 0.28f) {
            drawRect(Brush.verticalGradient(listOf(medio, lerp(medio, l.bruma, 0.35f)), y0, y1), Offset(x0, y0), Size(x1 - x0, y1 - y0))
            val w = (x1 - x0) * canto
            drawRect(Brush.verticalGradient(listOf(if (solDer) sombra else luz, lerp(if (solDer) sombra else luz, l.bruma, 0.35f)), y0, y1), Offset(x0, y0), Size(w, y1 - y0))
            drawRect(Brush.verticalGradient(listOf(if (solDer) luz else sombra, lerp(if (solDer) luz else sombra, l.bruma, 0.35f)), y0, y1), Offset(x1 - w, y0), Size(w, y1 - y0))
        }
        // Las alas y sus rendijas.
        cuerpo(120f, 194f, 100f, H + 6f, 0.12f)
        rendijas(l, Rect(123f, 103f, 191f, H + 2f), 2.6f, 3.6f, enc, lejos, az)
        drawRect(l.tono(C.HORMIGON_OSC, lejos), Offset(119f, 99f), Size(76f, 1.6f))
        // El fuste: nervios de arriba abajo y, entre ellos, las rendijas.
        cuerpo(140f, 174f, 44f, H + 6f)
        var x = 142.4f
        while (x < 172f) { drawLine(lerp(medio, l.tono(C.HORMIGON_OSC, lejos), 0.5f), Offset(x, 46f), Offset(x, H + 4f), 0.5f); x += 4.2f }
        rendijas(l, Rect(144f, 62f, 171f, 100f), 4.2f, 3.2f, enc, lejos, az)
        // Las chorreras de la lluvia en el hormigón y la mancha de humedad del pie.
        clipRect(140f, 44f, 174f, H + 6f) { repeat(9) { val sx = az.entre(140f, 174f); drawRect(Brush.verticalGradient(listOf(l.tono(C.HORMIGON_OSC, lejos).copy(alpha = 0.3f), Color.Transparent), 44f, 44f + az.entre(10f, 40f)), Offset(sx, 44f), Size(az.entre(0.6f, 1.8f), 40f)) } }
        // La coronación escalonada, con el ojo.
        cuerpo(144f, 170f, 36f, 44f); cuerpo(149f, 165f, 29f, 36f)
        drawRect(l.tono(C.HORMIGON_OSC, lejos), Offset(139f, 43.4f), Size(36f, 1.2f)); drawRect(l.tono(C.HORMIGON_OSC, lejos), Offset(143f, 35.6f), Size(28f, 1f))
        ojo(Offset(157f, 53f), 20f, l.tono(C.ROJO, lejos * 0.7f), l.tono(C.ROJO_OSC, lejos * 0.7f))
        // Las colgaduras: en campaña y los días de desfile.
        if (EscenaDistopia.enCampana(m) || EscenaDistopia.desfile(m)) for (bx in listOf(144.6f, 166.2f)) {
            drawRect(Brush.verticalGradient(listOf(l.tono(C.ROJO, lejos * 0.6f), l.tono(C.ROJO_OSC, lejos * 0.6f)), 60f, 112f), Offset(bx, 60f), Size(3.4f, 52f))
            drawPath(poligono(Offset(bx, 112f), Offset(bx + 3.4f, 112f), Offset(bx + 1.7f, 115f)), l.tono(C.ROJO_OSC, lejos * 0.6f))
        }
        // La bandera enorme de la azotea; a media asta y con crespón en el luto.
        val asta = Offset(157f, 29f); val tope = Offset(157f, 9f)
        drawLine(l.tono(C.HIERRO, lejos), asta, tope, 0.8f)
        val izada = if (EscenaDistopia.luto(m)) tope + Offset(0f, 8f) else tope + Offset(0f, 0.6f)
        val viento = 0.5f + 0.5f * m.viento
        val pano = Path().apply { moveTo(izada.x, izada.y); cubicTo(izada.x + 6f, izada.y - 1.6f * viento, izada.x + 12f, izada.y + 2.2f * viento, izada.x + 19f, izada.y + 0.6f); lineTo(izada.x + 18.4f, izada.y + 10.6f); cubicTo(izada.x + 12f, izada.y + 12f * (0.9f + 0.1f * viento), izada.x + 6f, izada.y + 8.4f, izada.x, izada.y + 10f); close() }
        drawPath(pano, Brush.horizontalGradient(listOf(l.tono(C.ROJO, 0.2f), l.tono(lerp(C.ROJO, C.LUZ, 0.25f), 0.2f), l.tono(C.ROJO_OSC, 0.2f), l.tono(C.ROJO, 0.2f)), izada.x, izada.x + 19f))
        ojo(izada + Offset(9.4f, 4.9f), 11f, l.tono(C.HUESO, 0.2f), l.tono(C.ROJO_OSC, 0.2f))
        drawLine(l.tono(C.HUESO, 0.2f), izada + Offset(1.4f, 8.6f), izada + Offset(17.4f, 9f), 0.8f)
        if (EscenaDistopia.luto(m)) drawLine(l.tono(C.HOLLIN, 0.2f), tope, tope + Offset(5f, 9f), 1.2f, StrokeCap.Round)
        // De noche lo alumbran desde abajo: el hormigón blanco es lo que más se ve de la capital.
        if (l.noche > 0.3f && EscenaDistopia.corriente(m)) {
            val k = ((l.noche - 0.3f) / 0.5f).coerceIn(0f, 1f)
            val foco = lerp(C.SODIO, Color.White, 0.55f)
            // Los focos del pie bañan el fuste de abajo arriba; la coronación y las alas reciben lo que sobra.
            clipRect(120f, 29f, 194f, H + 6f) {
                drawRect(Brush.verticalGradient(0f to foco.copy(alpha = 0.1f * k), 0.45f to foco.copy(alpha = 0.3f * k), 0.8f to foco.copy(alpha = 0.46f * k), 1f to foco.copy(alpha = 0.3f * k), startY = 29f, endY = H + 6f), Offset(140f, 29f), Size(34f, 105f), blendMode = BlendMode.Plus)
                drawRect(Brush.verticalGradient(listOf(foco.copy(alpha = 0.03f * k), foco.copy(alpha = 0.16f * k)), 100f, H + 6f), Offset(120f, 100f), Size(74f, 34f), blendMode = BlendMode.Plus)
            }
            haloDeBruma(Offset(157f, H - 2f), 40f, 16f, foco, 0.22f * k)
            drawCircle(C.PILOTO.copy(alpha = 0.95f), 1f, tope)
        }
    }

    /**
     * La torre nueva del ministerio, a su derecha: un solar con su grúa, luego la estructura que sube piso a piso con
     * los andamios y, desde que se inaugura, la torre más alta de la capital.
     */
    private fun DrawScope.torreNueva(m: Momento, l: Luces, r: Rayo) {
        val lejos = 0.46f
        val x0 = 181f; val x1 = 205f; val pie = H + 6f; val cimaFinal = 24f
        val a = EscenaDistopia.alturaDeLaTorre(m); val estado = EscenaDistopia.torre(m)
        val az = Azar(Semilla.de("distopia", "torre nueva"))
        val propio = lerp(C.CAL, C.HORMIGON, 0.35f)
        val luz = cara(l, r, propio, lado(r, 1f).coerceAtLeast(0.1f), lejos); val sombra = cara(l, r, propio, lado(r, -1f).coerceAtMost(-0.3f), lejos); val medio = cara(l, r, propio, frente(r), lejos)
        val hierro = l.tono(C.HIERRO, lejos); val oxido = l.tono(C.OXIDO, lejos)
        fun grua(base: Offset, alto: Float, pluma: Float, haciaLaDerecha: Boolean) {
            val s = if (haciaLaDerecha) 1f else -1f; val tope = base + Offset(0f, -alto)
            drawLine(oxido, base, tope, 1.2f)
            var y = base.y; while (y > tope.y + 2f) { drawLine(hierro.copy(alpha = 0.7f), Offset(base.x - 0.6f, y), Offset(base.x + 0.6f, y - 2f), 0.3f); y -= 2f }
            drawLine(oxido, tope + Offset(-s * pluma * 0.3f, 1.4f), tope + Offset(s * pluma, 1.4f), 0.9f)
            drawLine(hierro, tope + Offset(0f, -3.4f), tope + Offset(s * pluma, 1.2f), 0.3f); drawLine(hierro, tope + Offset(0f, -3.4f), tope + Offset(-s * pluma * 0.3f, 1.2f), 0.3f); drawLine(oxido, tope, tope + Offset(0f, -3.6f), 0.8f)
            drawRect(l.tono(C.HORMIGON_OSC, lejos), tope + Offset(-s * pluma * 0.3f - 1f, 1.4f), Size(2.4f, 2.4f))
            val gancho = tope + Offset(s * pluma * 0.7f, 1.6f); val cuelga = if (m.hacer == Hacer.TRABAJO) alto * 0.45f else alto * 0.14f
            drawLine(hierro, gancho, gancho + Offset(0f, cuelga), 0.3f); if (m.hacer == Hacer.TRABAJO) drawRect(l.tono(C.HORMIGON, lejos), gancho + Offset(-1.6f, cuelga), Size(3.2f, 1.6f))
            if (l.noche > 0.4f && EscenaDistopia.corriente(m)) drawCircle(C.PILOTO.copy(alpha = 0.9f), 0.8f, tope + Offset(0f, -3.6f))
        }
        if (estado == Torre.TERMINADA) {
            // El fuste, la coronación y la aguja: más alta que el ministerio.
            fun cuerpo(a0: Float, a1: Float, y0: Float, y1: Float) {
                drawRect(Brush.verticalGradient(listOf(medio, lerp(medio, l.bruma, 0.35f)), y0, y1), Offset(a0, y0), Size(a1 - a0, y1 - y0))
                val w = (a1 - a0) * 0.3f
                drawRect(if (r.lat >= 0f) sombra else luz, Offset(a0, y0), Size(w, y1 - y0)); drawRect(if (r.lat >= 0f) luz else sombra, Offset(a1 - w, y0), Size(w, y1 - y0))
            }
            cuerpo(x0, x1, 40f, pie); cuerpo(x0 + 3f, x1 - 3f, 31f, 40f); cuerpo(x0 + 7f, x1 - 7f, cimaFinal, 31f)
            drawLine(hierro, Offset((x0 + x1) / 2f, cimaFinal), Offset((x0 + x1) / 2f, 8f), 0.7f)
            var x = x0 + 3f; while (x < x1 - 1f) { drawLine(lerp(medio, l.tono(C.HORMIGON_OSC, lejos), 0.5f), Offset(x, 42f), Offset(x, pie), 0.4f); x += 3.6f }
            rendijas(l, Rect(x0 + 4.4f, 46f, x1 - 2f, H), 3.6f, 3.2f, EscenaDistopia.rendijas(m), lejos, az)
            ojo(Offset((x0 + x1) / 2f, 36f), 12f, l.tono(C.ROJO, lejos * 0.7f), l.tono(C.ROJO_OSC, lejos * 0.7f))
            if (EscenaDistopia.desfile(m)) drawRect(Brush.verticalGradient(listOf(l.tono(C.ROJO, lejos * 0.6f), l.tono(C.ROJO_OSC, lejos * 0.6f)), 46f, 104f), Offset((x0 + x1) / 2f - 1.8f, 46f), Size(3.6f, 58f))
            if (l.noche > 0.3f && EscenaDistopia.corriente(m)) { drawCircle(C.PILOTO.copy(alpha = 0.95f), 1f, Offset((x0 + x1) / 2f, 8f)); drawRect(Brush.verticalGradient(listOf(Color.Transparent, lerp(C.SODIO, Color.White, 0.5f).copy(alpha = 0.26f)), 40f, pie), Offset(x0, 24f), Size(x1 - x0, pie - 24f), blendMode = BlendMode.Plus) }
            return
        }
        // En obra. El solar vallado.
        drawRect(l.tono(C.OXIDO, lejos), Offset(x0 - 3f, H - 1.4f), Size(x1 - x0 + 6f, 3f))
        val cima = pie - (pie - 40f) * (a / 0.97f)
        if (estado != Torre.CIMIENTOS) {
            // Lo ya cerrado, abajo; arriba, los últimos pisos en esqueleto: pilares y forjados con el cielo detrás.
            val cerrado = cima + (pie - cima) * 0.22f + 4f
            drawRect(Brush.verticalGradient(listOf(medio, lerp(medio, l.bruma, 0.35f)), cerrado, pie), Offset(x0, cerrado), Size(x1 - x0, pie - cerrado))
            drawRect(if (r.lat >= 0f) sombra else luz, Offset(x0, cerrado), Size(7f, pie - cerrado)); drawRect(if (r.lat >= 0f) luz else sombra, Offset(x1 - 7f, cerrado), Size(7f, pie - cerrado))
            var y = cerrado; while (y > cima) { drawRect(l.tono(C.HORMIGON_OSC, lejos), Offset(x0, y - 0.8f), Size(x1 - x0, 0.8f)); y -= 3.2f }
            var x = x0; while (x <= x1 + 0.1f) { drawLine(l.tono(C.HORMIGON_OSC, lejos), Offset(x, cerrado), Offset(x, cima - (if ((x - x0).toInt() % 8 == 0) 2f else 0f)), 0.7f); x += 4f }
            // El andamio, del lado de la avenida, con su malla.
            drawRect(l.tono(C.VERDE_GRIS, lejos).copy(alpha = 0.6f), Offset(x0 - 2.2f, cima + 2f), Size(2.4f, (pie - cima) * 0.6f))
            var ay = cima + 2f; while (ay < cima + 2f + (pie - cima) * 0.6f) { drawLine(hierro.copy(alpha = 0.7f), Offset(x0 - 2.4f, ay), Offset(x0 + 0.4f, ay), 0.3f); ay += 3.2f }
        } else {
            // Los cimientos: el encofrado y unas armaduras.
            for (i in 0..4) drawLine(oxido, Offset(x0 + 2f + i * 5f, H + 1f), Offset(x0 + 2f + i * 5f, H - 3f - az.entre(0f, 3f)), 0.5f)
            drawRect(l.tono(C.MADERA, lejos), Offset(x0, H - 1.6f), Size(x1 - x0, 2.6f))
        }
        // Las grúas: una sube con la torre; otra, más baja, a su lado. En el remate ya solo queda la de arriba.
        grua(Offset(x1 - 5f, cima.coerceAtMost(H)), if (estado == Torre.CIMIENTOS) 34f else 18f, 16f, haciaLaDerecha = false)
        if (estado != Torre.REMATE) grua(Offset(x1 + 7f, H + 2f), (H - cima).coerceAtLeast(0f) * 0.62f + 46f, 17f, haciaLaDerecha = true)
    }

    /** Los reflectores de la azotea del ministerio: se encienden las noches de campaña, de desfile y de cohetes. */
    private fun DrawScope.reflectores(m: Momento, l: Luces) {
        if (l.noche < 0.4f || !EscenaDistopia.corriente(m)) return
        if (!(EscenaDistopia.enCampana(m) || EscenaDistopia.desfile(m) || EscenaDistopia.cohetes(m))) return
        val az = m.azar("reflectores")
        for (o in listOf(Offset(146f, 36f), Offset(168f, 36f), Offset(124f, 100f))) {
            val ang = az.entre(-2.5f, -0.7f); val largo = az.entre(90f, 150f)
            val fin = o + Offset(cos(ang) * largo, sin(ang) * largo); val n = Offset(-sin(ang), cos(ang)) * (largo * 0.09f)
            drawPath(poligono(o, fin + n, fin - n), Brush.linearGradient(listOf(Color(0xFFEFF4E0).copy(alpha = 0.3f * l.noche), Color.Transparent), o, fin), blendMode = BlendMode.Plus)
        }
    }

    // ---------------------------------------------------------------- Los bloques

    /** La ventana `(col, fila)` del bloque [n]: un número fijo de 0 a 1 para sortear si tiene luz, si está tapiada o si hay ropa. */
    private fun suerte(n: Int, col: Int, fila: Int, que: Int): Float = Azar(Semilla.de("distopia", "ventana", n, col, fila, que)).real()

    /** La ventana del vecino de enfrente, en el bloque más cercano. */
    private const val VECINO_COL = 2
    private const val VECINO_FILA = 8

    /**
     * Un bloque de la avenida: el testero, que mira a la plaza, y la fachada, que se aleja con ella. Todos salen del
     * mismo plano, pero cada uno tiene sus manchas, sus ventanas tapiadas, su ropa tendida y sus antenas.
     */
    private fun DrawScope.bloque(m: Momento, l: Luces, r: Rayo, b: Bloque) {
        val az = Azar(Semilla.de("distopia", "bloque", b.n))
        val lejos = lejosDe((b.u0 + b.u1) / 2f)
        val propio = lerp(C.HORMIGON, listOf(Color(0xFFB6B2A0), Color(0xFFC8C8BC), Color(0xFFAEB6AA))[b.n % 3], 0.7f)
        val x0 = b.fx(b.u0); val x1 = b.fx(b.u1); val t0 = b.cima(b.u0); val t1 = b.cima(b.u1); val g0 = gy(b.u0); val g1 = gy(b.u1)
        val xt = x0 + b.lado * b.fondo * b.u0
        val enc = EscenaDistopia.ventanas(m); val noche = l.noche > 0.3f; val corriente = EscenaDistopia.corriente(m)
        val luzCasa = Color(0xFFFFD48A); val hueco = l.tono(lerp(C.CRISTAL, C.HOLLIN, 0.3f), lejos)
        val sucio = l.tono(lerp(C.HORMIGON_OSC, C.HOLLIN, 0.4f), lejos)

        // El testero.
        val tx0 = minOf(x0, xt); val tx1 = maxOf(x0, xt)
        val cTestero = cara(l, r, propio, frente(r), lejos)
        drawRect(Brush.verticalGradient(listOf(lerp(cTestero, l.tono(C.LUZ, lejos), 0.08f), cTestero, lerp(cTestero, sucio, 0.3f)), t0, g0), Offset(tx0, t0), Size(tx1 - tx0, g0 - t0))
        clipRect(tx0, t0, tx1, g0) {
            // Las juntas del encofrado, las chorreras y la humedad que sube.
            val pisoAlto = (g0 - t0) / b.pisos
            for (i in 1 until b.pisos) drawLine(sucio.copy(alpha = 0.22f), Offset(tx0, t0 + pisoAlto * i), Offset(tx1, t0 + pisoAlto * i), 0.4f * b.u0 + 0.2f)
            var jx = tx0 + 14f * b.u0; while (jx < tx1) { drawLine(sucio.copy(alpha = 0.14f), Offset(jx, t0), Offset(jx, g0), 0.3f); jx += 14f * b.u0 }
            repeat((10 * b.u0).toInt() + 3) { val sx = az.entre(tx0, tx1); val sh = az.entre(0.1f, 0.5f) * (g0 - t0); drawRect(Brush.verticalGradient(listOf(sucio.copy(alpha = 0.34f), Color.Transparent), t0, t0 + sh), Offset(sx, t0), Size(az.entre(0.8f, 3f) * b.u0 + 0.3f, sh)) }
            repeat(5) { mancha(Offset(az.entre(tx0, tx1), az.entre(t0, g0)), az.entre(8f, 26f) * b.u0, az.entre(6f, 20f) * b.u0, if (it % 2 == 0) sucio else l.tono(C.LUZ, lejos), 0.12f) }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sucio.copy(alpha = 0.4f)), g0 - 16f * b.u0, g0), Offset(tx0, g0 - 16f * b.u0), Size(tx1 - tx0, 16f * b.u0))
            if (b.ciego) testeroCiego(m, l, r, tx0, tx1, t0, g0)
            else {
                // Una columna de ventanas de escalera.
                val cx = tx0 + (tx1 - tx0) * 0.5f
                for (i in 0 until b.pisos) { val y = t0 + pisoAlto * (i + 0.3f); val t = suerte(b.n, -1, i, 0)
                    drawRect(if (noche && corriente && !EscenaDistopia.toqueDeQueda(m) && t < 0.7f) luzCasa.copy(alpha = 0.55f) else hueco, Offset(cx - 1.4f * b.u0 - 0.3f, y), Size(2.8f * b.u0 + 0.6f, pisoAlto * 0.45f)) }
            }
        }

        // La fachada que se aleja.
        val fachada = poligono(Offset(x0, t0), Offset(x1, t1), Offset(x1, g1), Offset(x0, g0))
        val cFachada = cara(l, r, propio, lado(r, -b.lado), lejos); val cLejos = cara(l, r, propio, lado(r, -b.lado), lejosDe(b.u1))
        drawPath(fachada, Brush.horizontalGradient(listOf(cFachada, cLejos), x0, x1))
        fun en(u: Float, k: Float) = Offset(b.fx(u), b.cima(u) + (gy(u) - b.cima(u)) * k)   // el punto de la fachada a la profundidad u y a la altura k (0 = el alero; 1 = el suelo)
        clipPath(fachada) {
            for (i in 1 until b.pisos) drawLine(sucio.copy(alpha = 0.2f), en(b.u0, i / b.pisos.toFloat()), en(b.u1, i / b.pisos.toFloat()), 0.3f + 0.3f * b.u0)
            repeat(6) { val u = az.entre(b.u1, b.u0); val k = az.entre(0f, 0.6f); val p = en(u, k); drawRect(Brush.verticalGradient(listOf(sucio.copy(alpha = 0.3f), Color.Transparent), p.y, p.y + 30f * u), p, Size(1.2f * u + 0.4f, 30f * u)) }
            // El zócalo: los bajos, más sucios, con el portal.
            drawPath(poligono(en(b.u0, 1f - 1f / b.pisos), en(b.u1, 1f - 1f / b.pisos), en(b.u1, 1f), en(b.u0, 1f)), sucio.copy(alpha = 0.34f))
        }
        val du = (b.u0 - b.u1) / b.cols
        for (j in 0 until b.cols) {
            val ua = b.u0 - du * (j + 0.22f); val ub = b.u0 - du * (j + 0.78f)
            for (i in 0 until b.pisos - 1) {
                val ka = (i + 0.28f) / b.pisos; val kb = (i + 0.74f) / b.pisos
                val forma = poligono(en(ua, ka), en(ub, ka), en(ub, kb), en(ua, kb))
                val t = suerte(b.n, j, i, 0); val tapiada = suerte(b.n, j, i, 1) < 0.045f
                val delVecino = b.n == 0 && j == VECINO_COL && i == VECINO_FILA
                val vive = if (delVecino) EscenaDistopia.vecinoDeEnfrente(m) else !tapiada
                if (!vive) {
                    drawPath(forma, l.tono(C.MADERA, lejos))
                    if (b.u0 > 0.5f) { drawLine(l.tono(C.HOLLIN, lejos).copy(alpha = 0.6f), en(ua, ka), en(ub, kb), 0.4f); drawLine(l.tono(C.HOLLIN, lejos).copy(alpha = 0.6f), en(ub, ka), en(ua, kb), 0.4f) }
                    continue
                }
                val conLuz = noche && (t < enc || (delVecino && enc > 0f))
                when {
                    conLuz -> drawPath(forma, (if (corriente) luzCasa else Color(0xFFFFB45A)).copy(alpha = (0.95f - 0.5f * lejos) * (if (corriente) 1f else 0.7f)))
                    noche && corriente -> { drawPath(forma, hueco); if (suerte(b.n, j, i, 2) < 0.7f) drawPath(forma, C.PANTALLA.copy(alpha = 0.22f * (1f - lejos))) }   // la pantalla de cada cuarto, que no se apaga
                    else -> {
                        drawPath(forma, cara(l, r, C.CRISTAL, lado(r, -b.lado) * 0.5f, lejos))
                        if (b.u0 > 0.3f && r.dia > 0.3f) drawPath(poligono(en(ua, ka), en(ub, ka), en(ub, ka + (kb - ka) * 0.35f)), l.tono(lerp(C.CRISTAL, Color.White, 0.5f), lejos).copy(alpha = 0.5f))
                    }
                }
                // El retrato sin cara de cada ventana, el Día del Fundador y los desfiles; el tiesto del vecino; la ropa tendida.
                if (EscenaDistopia.desfile(m) && suerte(b.n, j, i, 3) < 0.8f) drawPath(poligono(en(ua, ka + (kb - ka) * 0.45f), en(ub, ka + (kb - ka) * 0.45f), en(ub, kb), en(ua, kb)), l.tono(C.ROJO, lejos))
                if (delVecino && b.u0 > 0.5f) { val p = en((ua + ub) / 2f, kb); drawCircle(l.tono(Color(0xFF5E8A4E), lejos), 1.3f, p + Offset(0f, -1.6f)); drawCircle(l.tono(C.ROJO, lejos), 0.6f, p + Offset(0.3f, -2.4f)); drawRect(l.tono(C.OXIDO, lejos), p + Offset(-0.9f, -0.8f), Size(1.8f, 1f)) }
                if (b.u0 > 0.3f && !EscenaDistopia.desfile(m) && Azar(m.semilla("ropa", b.n, j, i)).real() < 0.1f && !EscenaDistopia.mojado(m)) {
                    val a0 = en(ua, kb + 0.012f); val a1 = en(ub, kb + 0.012f); val color = listOf(C.HUESO, Color(0xFF8E9AA8), C.SACO, Color(0xFFA8B49A))[(j + i) % 4]
                    drawPath(poligono(a0, a1, a1 + Offset(0f, 2.6f * b.u0), a0 + Offset(0f, 3.4f * b.u0)), l.tono(color, lejos))
                }
            }
        }
        // El portal, con su marquesina.
        val up = b.u0 - du * (b.cols / 2 + 0.5f)
        drawPath(poligono(en(up + du * 0.3f, 1f - 0.8f / b.pisos), en(up - du * 0.3f, 1f - 0.8f / b.pisos), en(up - du * 0.3f, 1f), en(up + du * 0.3f, 1f)), l.tono(C.HOLLIN, lejos))
        drawLine(l.tono(C.HORMIGON_OSC, lejos), en(up + du * 0.5f, 1f - 0.86f / b.pisos), en(up - du * 0.5f, 1f - 0.86f / b.pisos) + Offset(-b.lado * 1f, 0f), 0.8f * b.u0 + 0.3f)
        // La esquina, con su filo, y el alero.
        drawLine(lerp(cFachada, l.tono(C.LUZ, lejos), 0.3f), Offset(x0, t0), Offset(x0, g0), 0.5f)
        drawLine(l.tono(C.HORMIGON_OSC, lejos), Offset(xt, t0), Offset(x0, t0), 0.9f * b.u0 + 0.3f); drawLine(l.tono(C.HORMIGON_OSC, lejos), Offset(x0, t0), Offset(x1, t1), 0.9f * b.u0 + 0.3f)
        // Lo de la azotea: antenas y un depósito, distintos en cada bloque.
        val hierro = l.tono(C.HIERRO, lejos)
        repeat(2 + b.n % 3) { val u = az.entre(b.u1, b.u0); val p = Offset(b.fx(u), b.cima(u)); val h = az.entre(5f, 11f) * u + 2f
            drawLine(hierro, p, p + Offset(0f, -h), 0.3f + 0.3f * u); for (k in 1..2) drawLine(hierro, p + Offset(-1.6f * u - 0.6f, -h * k / 2.4f), p + Offset(1.6f * u + 0.6f, -h * k / 2.4f), 0.25f + 0.2f * u) }
        if (b.n % 2 == 0) { val u = (b.u0 + b.u1) / 2f; val p = Offset(b.fx(u) + b.lado * 6f * u, b.cima(u)); drawRect(cara(l, r, C.HORMIGON_OSC, frente(r), lejos), p + Offset(-3f * u, -5f * u), Size(6f * u, 5f * u)) }
        if (EscenaDistopia.nevado(m)) { drawLine(l.tono(C.NIEVE, lejos), Offset(xt, t0 - 0.4f), Offset(x0, t0 - 0.4f), 1.2f * b.u0 + 0.4f); drawLine(l.tono(C.NIEVE, lejos), Offset(x0, t0 - 0.4f), Offset(x1, t1 - 0.3f), 1.2f * b.u0 + 0.4f) }
    }

    /**
     * El testero ciego del bloque más cercano, a la izquierda: una pared enorme de hormigón con el ojo pintado, ya
     * desvaído, una bajante con su óxido y, los días de desfile, la colgadura grande.
     */
    private fun DrawScope.testeroCiego(m: Momento, l: Luces, r: Rayo, x0: Float, x1: Float, y0: Float, y1: Float) {
        val az = Azar(Semilla.de("distopia", "testero"))
        // El mural: una franja roja desvaída y el ojo, con la pintura saltada.
        val c = Offset(24f, 104f)
        drawRect(l.tono(C.ROJO, 0.1f).copy(alpha = 0.3f), Offset(x0, 84f), Size(x1 - x0, 40f))
        ojo(c, 44f, l.tono(C.HUESO, 0.08f), l.tono(C.ROJO_OSC, 0.08f), 0.66f)
        repeat(34) { val p = Offset(az.entre(x0, x1), az.entre(82f, 126f)); drawOval(l.tono(C.HORMIGON, 0.08f).copy(alpha = 0.6f), p, Size(az.entre(1.5f, 7f), az.entre(0.8f, 3f))) }
        // La bajante y su chorrera de óxido.
        drawLine(l.tono(C.HIERRO, 0.08f), Offset(47.6f, y0), Offset(47.6f, y1), 1.6f); drawLine(l.tono(lerp(C.HIERRO, Color.White, 0.3f), 0.08f), Offset(47.1f, y0), Offset(47.1f, y1), 0.4f)
        for (y in listOf(60f, 112f, 160f)) { drawRect(l.tono(C.HOLLIN, 0.08f), Offset(46.2f, y), Size(2.8f, 1.6f)); drawRect(Brush.verticalGradient(listOf(l.tono(C.OXIDO, 0.08f).copy(alpha = 0.5f), Color.Transparent), y, y + 22f), Offset(45.6f, y), Size(3.2f, 22f)) }
        // Los restos de carteles pegados al pie, unos sobre otros.
        repeat(7) { val p = Offset(az.entre(x0, 40f), az.entre(160f, 190f)); drawRect(l.tono(listOf(C.HUESO, C.ROJO, C.SACO, C.VERDE_GRIS)[it % 4], 0.08f).copy(alpha = 0.7f), p, Size(az.entre(5f, 10f), az.entre(6f, 12f))) }
        if (EscenaDistopia.desfile(m)) {
            val a = Offset(4f, 58f); val w = 34f; val largo = 104f
            val pano = poligono(listOf(a, a + Offset(w, 0f), a + Offset(w, largo), a + Offset(w / 2f, largo - 8f), a + Offset(0f, largo)))
            drawPath(pano, Brush.horizontalGradient(listOf(l.tono(C.ROJO_OSC), l.tono(C.ROJO), l.tono(lerp(C.ROJO, C.LUZ, 0.2f)), l.tono(C.ROJO)), a.x, a.x + w))
            for (k in listOf(0.3f, 0.66f)) drawLine(l.tono(C.ROJO_OSC).copy(alpha = 0.6f), a + Offset(w * k, 0f), a + Offset(w * k - 0.6f, largo - 4f), 0.8f)
            ojo(a + Offset(w / 2f, 46f), 26f, l.tono(C.HUESO), l.tono(C.ROJO_OSC))
            drawLine(l.tono(C.MADERA), a + Offset(-2f, 0f), a + Offset(w + 2f, 0f), 1.6f, StrokeCap.Round)
        }
    }

    /** Los bloques de enfrente, al otro lado de la plaza: tres, de cara, iguales y cada uno a su manera; delante, la tapia. */
    private fun DrawScope.bloquesDeEnfrente(m: Momento, l: Luces, r: Rayo) {
        val enc = EscenaDistopia.ventanas(m); val noche = l.noche > 0.3f; val corriente = EscenaDistopia.corriente(m); val lejos = 0.34f
        for ((n, zona) in listOf(Rect(263f, 107f, 299f, 170f), Rect(304f, 100f, 344f, 170f), Rect(349f, 109f, 392f, 170f)).withIndex()) {
            val az = Azar(Semilla.de("distopia", "enfrente", n))
            val propio = lerp(C.HORMIGON, listOf(Color(0xFFB8B4A2), Color(0xFFC6C6BA), Color(0xFFB0B8AC))[n], 0.7f)
            val c = cara(l, r, propio, frente(r), lejos); val sucio = l.tono(lerp(C.HORMIGON_OSC, C.HOLLIN, 0.4f), lejos)
            drawRect(Brush.verticalGradient(listOf(c, lerp(c, l.bruma, 0.25f)), zona.top, zona.bottom), zona.topLeft, zona.size)
            // El canto que da (o no) al sol.
            drawRect(cara(l, r, propio, lado(r, 1f), lejos), Offset(zona.right - 2.6f, zona.top), Size(2.6f, zona.height)); drawRect(cara(l, r, propio, lado(r, -1f), lejos), Offset(zona.left, zona.top), Size(2.6f, zona.height))
            clipRect(zona.left, zona.top, zona.right, zona.bottom) { repeat(5) { val sx = az.entre(zona.left, zona.right); drawRect(Brush.verticalGradient(listOf(sucio.copy(alpha = 0.3f), Color.Transparent), zona.top, zona.top + az.entre(8f, 30f)), Offset(sx, zona.top), Size(az.entre(0.8f, 2f), 30f)) } }
            val pisoAlto = 5.4f; val cada = 4.6f
            var y = zona.top + 2.4f; var fila = 0
            while (y < zona.bottom - 4f) {
                drawLine(sucio.copy(alpha = 0.2f), Offset(zona.left, y - 1.2f), Offset(zona.right, y - 1.2f), 0.3f)
                var x = zona.left + 4f; var col = 0
                while (x < zona.right - 4.6f) {
                    val t = suerte(10 + n, col, fila, 0); val tapiada = suerte(10 + n, col, fila, 1) < 0.05f
                    val color = when {
                        tapiada -> l.tono(C.MADERA, lejos)
                        noche && t < enc -> (if (corriente) Color(0xFFFFD48A) else Color(0xFFFFB45A)).copy(alpha = if (corriente) 0.85f else 0.6f)
                        noche && corriente && suerte(10 + n, col, fila, 2) < 0.7f -> lerp(l.tono(C.CRISTAL, lejos), C.PANTALLA, 0.22f)
                        else -> cara(l, r, lerp(C.CRISTAL, C.HOLLIN, 0.2f), frente(r), lejos)
                    }
                    drawRect(color, Offset(x, y), Size(2.4f, 2.8f))
                    if (EscenaDistopia.desfile(m) && !tapiada && suerte(10 + n, col, fila, 3) < 0.8f) drawRect(l.tono(C.ROJO, lejos), Offset(x, y + 1.4f), Size(2.4f, 1.4f))
                    if (!tapiada && !EscenaDistopia.desfile(m) && !EscenaDistopia.mojado(m) && Azar(m.semilla("ropa", 10 + n, col, fila)).real() < 0.07f) drawRect(l.tono(listOf(C.HUESO, Color(0xFF8E9AA8), C.SACO)[(col + fila) % 3], lejos), Offset(x - 0.2f, y + 3f), Size(2.8f, 1.5f))
                    x += cada; col++
                }
                y += pisoAlto; fila++
            }
            drawRect(l.tono(C.HORMIGON_OSC, lejos), Offset(zona.left - 0.6f, zona.top - 1f), Size(zona.width + 1.2f, 1.4f))
            repeat(2) { val ax = az.entre(zona.left + 4f, zona.right - 4f); val h = az.entre(4f, 8f); drawLine(l.tono(C.HIERRO, lejos), Offset(ax, zona.top), Offset(ax, zona.top - h), 0.4f); drawLine(l.tono(C.HIERRO, lejos), Offset(ax - 1.6f, zona.top - h * 0.7f), Offset(ax + 1.6f, zona.top - h * 0.7f), 0.3f) }
            if (EscenaDistopia.nevado(m)) drawRect(l.tono(C.NIEVE, lejos), Offset(zona.left - 0.6f, zona.top - 1.8f), Size(zona.width + 1.2f, 1.2f))
        }
    }

    // ---------------------------------------------------------------- El suelo de la avenida

    /** El suelo, del horizonte al borde de la plaza: la calzada de asfalto con su raya y su paso de cebra, las aceras y, a la derecha, el fondo de la plaza. */
    private fun DrawScope.suelo(m: Momento, l: Luces, r: Rayo, az: Azar) {
        val mojado = EscenaDistopia.mojado(m); val nieve = EscenaDistopia.nevado(m)
        val losa = if (nieve) lerp(C.LOSA, C.NIEVE, 0.6f) else if (mojado) lerp(C.LOSA, C.ASFALTO, 0.4f) else C.LOSA
        val asfalto = if (nieve) lerp(C.ASFALTO, C.NIEVE, 0.35f) else if (mojado) lerp(C.ASFALTO, C.HOLLIN, 0.4f) else C.ASFALTO
        drawRect(Brush.verticalGradient(listOf(l.tono(losa, 0.6f), l.tono(losa, 0.08f)), H, BORDE), Offset(0f, H), Size(ANCHO, BORDE - H + 1f))
        // La calzada.
        val calzada = poligono(Offset(bordilloIzq(1f), BORDE + 1f), Offset(bordilloIzq(0.02f), gy(0.02f)), Offset(bordilloDer(0.02f), gy(0.02f)), Offset(bordilloDer(1f), BORDE + 1f))
        drawPath(calzada, Brush.verticalGradient(listOf(l.tono(asfalto, 0.6f), l.tono(asfalto, 0.3f), l.tono(asfalto, 0.06f)), H, BORDE))
        clipPath(calzada) {
            // Las manchas del asfalto, más marcadas cuanto más cerca.
            repeat(22) { val u = az.entre(0.15f, 1f); val x = az.entre(bordilloIzq(u), bordilloDer(u)); mancha(Offset(x, gy(u)), az.entre(8f, 26f) * u, az.entre(1f, 3f) * u, l.tono(if (it % 2 == 0) C.HOLLIN else C.LOSA, lejosDe(u)), 0.1f + 0.2f * u) }
            // Los parches: bacheos de otro asfalto, con su cerco de brea.
            for ((n, q) in listOf(listOf(-82f, -62f, 0.84f, 0.72f), listOf(8f, 36f, 0.8f, 0.71f), listOf(-30f, -8f, 0.6f, 0.53f), listOf(66f, 90f, 0.78f, 0.69f), listOf(-74f, -58f, 0.46f, 0.41f), listOf(14f, 40f, 0.42f, 0.36f), listOf(-20f, 2f, 0.3f, 0.26f)).withIndex()) {
                val parche = poligono(enVia(q[0], q[2]), enVia(q[1], q[2]), enVia(q[1] + az.entre(-2f, 2f), q[3]), enVia(q[0] + az.entre(-2f, 2f), q[3]))
                drawPath(parche, l.tono(if (n % 3 == 0) lerp(C.ASFALTO, C.LOSA, 0.4f) else C.HOLLIN, lejosDelAsfalto(q[2])).copy(alpha = if (nieve) 0.15f else 0.2f + 0.3f * q[2]))
                drawPath(parche, l.tono(C.HOLLIN, lejosDelAsfalto(q[2])).copy(alpha = 0.3f + 0.3f * q[2]), style = Stroke(0.3f + 0.4f * q[2]))
            }
            // Las grietas selladas con brea.
            repeat(6) { val u0 = az.entre(0.4f, 0.95f); var X = az.entre(-80f, 84f); var u = u0
                val grieta = Path().apply { val p0 = enVia(X, u); moveTo(p0.x, p0.y); repeat(5) { X += az.entre(-9f, 9f); u *= az.entre(0.93f, 0.985f); val q = enVia(X, u); lineTo(q.x, q.y) } }
                drawPath(grieta, l.tono(C.HOLLIN, lejosDelAsfalto(u0)).copy(alpha = 0.45f), style = Stroke(0.4f + 0.5f * u0, cap = StrokeCap.Round, join = StrokeJoin.Round)) }
            // Las rodadas de los carriles, que huyen con la avenida y se borran a lo lejos.
            for (X in listOf(-78f, -68f, -24f, -12f, 18f, 30f, 72f, 84f))
                drawPath(poligono(enVia(X - 1.7f, 1.02f), enVia(X + 1.7f, 1.02f), enVia(X + 1.7f, 0.05f), enVia(X - 1.7f, 0.05f)), Brush.verticalGradient(listOf(l.tono(C.HOLLIN, 0.4f).copy(alpha = 0.03f), l.tono(C.HOLLIN, 0.1f).copy(alpha = 0.3f)), H, BORDE))
            vias(m, l, r, az, asfalto)
            // Las tapas de alcantarilla.
            for ((X, u) in listOf(-20f to 0.76f, 22f to 0.52f, -8f to 0.34f, 78f to 0.62f, -64f to 0.3f)) { val c = enVia(X, u); val lejos = lejosDelAsfalto(u)
                drawOval(l.tono(C.HOLLIN, lejos), Offset(c.x - 5.2f * u, c.y - 1.5f * u), Size(10.4f * u, 3f * u)); drawOval(l.tono(C.HIERRO, lejos), Offset(c.x - 4.4f * u, c.y - 1.2f * u), Size(8.8f * u, 2.4f * u))
                drawLine(l.tono(C.LUZ, lejos).copy(alpha = 0.35f), c + Offset(-3.6f * u, -0.8f * u), c + Offset(1.6f * u, -1.1f * u), 0.4f * u + 0.15f) }
            // La raya del centro, a trazos, y el paso de cebra, gastado.
            var u = 0.84f
            while (u > 0.06f) { val u1 = u * 0.84f; drawPath(poligono(Offset(VX + 3f * u, gy(u)), Offset(VX + 5.4f * u, gy(u)), Offset(VX + 5.4f * u1, gy(u1)), Offset(VX + 3f * u1, gy(u1))), l.tono(C.HUESO, lejosDe(u)).copy(alpha = 0.7f)); u *= 0.7f }
            for (k in -7..7) { val a = k * 11.4f + 1f
                drawPath(poligono(Offset(VX + (a - 3.2f) * 0.98f, gy(0.98f)), Offset(VX + (a + 3.2f) * 0.98f, gy(0.98f)), Offset(VX + (a + 3.2f) * 0.88f, gy(0.88f)), Offset(VX + (a - 3.2f) * 0.88f, gy(0.88f))), l.tono(C.HUESO, 0.08f).copy(alpha = 0.34f + 0.4f * az.real())) }
            if (mojado) drawPath(calzada, Brush.verticalGradient(listOf(l.bajo.copy(alpha = 0.34f), l.medio.copy(alpha = 0.12f)), H, BORDE))
        }
        // Los bordillos, con su filo claro, y las juntas de las aceras.
        for (f in listOf(::bordilloIzq, ::bordilloDer)) {
            drawLine(l.tono(C.HORMIGON, 0.1f), Offset(f(1f), BORDE + 1f), Offset(f(0.02f), gy(0.02f)), 1.4f)
            drawLine(l.tono(C.HORMIGON_OSC, 0.1f).copy(alpha = 0.7f), Offset(f(1f) + (if (f(1f) < VX) 1f else -1f), BORDE + 1.4f), Offset(f(0.02f), gy(0.02f) + 0.3f), 0.5f)
        }
        // Las bocacalles: el asfalto dobla la esquina entre bloque y bloque, y el bordillo se abre.
        for ((lado, ua, ub) in BOCACALLES) {
            val bord = if (lado < 0f) bordilloIzq(ua) to bordilloIzq(ub) else bordilloDer(ua) to bordilloDer(ub)
            val fuera = if (lado < 0f) -40f else ANCHO + 40f
            val boca = poligono(Offset(bord.first - lado * 1.2f, gy(ua)), Offset(fuera, gy(ua)), Offset(fuera, gy(ub)), Offset(bord.second - lado * 1.2f, gy(ub)))
            drawPath(boca, Brush.verticalGradient(listOf(l.tono(asfalto, lejosDelAsfalto(ub)), l.tono(asfalto, lejosDelAsfalto(ua))), gy(ub), gy(ua)))
            if (mojado) drawPath(boca, l.bajo.copy(alpha = 0.22f))
            drawLine(l.tono(C.HORMIGON, lejosDe(ua)), Offset(bord.first, gy(ua)), Offset(fuera, gy(ua)), 0.7f + 0.6f * ua); drawLine(l.tono(C.HORMIGON_OSC, lejosDe(ub)).copy(alpha = 0.8f), Offset(bord.second, gy(ub)), Offset(fuera, gy(ub)), 0.5f)
            // Su paso de peatones: unas pocas barras, medio borradas.
            if (lado < 0f) for (i in 0..3) { val uu = ua - (ua - ub) * (0.16f + 0.22f * i); val x = bordilloIzq(uu)
                drawPath(poligono(Offset(x - 3f * uu, gy(uu)), Offset(x - 16f * uu, gy(uu)), Offset(x - 16f * uu, gy(uu - 0.011f)), Offset(x - 3f * uu, gy(uu - 0.011f))), l.tono(C.HUESO, lejosDe(uu)).copy(alpha = 0.25f + 0.4f * az.real())) }
        }
        var u = 0.92f
        while (u > 0.1f) { drawLine(l.tono(C.HORMIGON_OSC, lejosDe(u)).copy(alpha = 0.4f), Offset(fachadaIzq(u), gy(u)), Offset(bordilloIzq(u), gy(u)), 0.4f); drawLine(l.tono(C.HORMIGON_OSC, lejosDe(u)).copy(alpha = 0.4f), Offset(bordilloDer(u), gy(u)), Offset(if (u > 0.5f) ANCHO else fachadaDer(u), gy(u)), 0.4f); u *= 0.86f }
        // El fondo de la plaza, a la derecha: las juntas que huyen y la tapia baja del otro lado.
        for (k in listOf(120f, 150f, 184f, 222f, 266f)) drawLine(l.tono(C.HORMIGON_OSC, 0.2f).copy(alpha = 0.34f), Offset(VX + k, BORDE + 1f), Offset(VX + k * 0.54f, gy(0.54f)), 0.4f)
        val tapia = Rect(260f, 163f, ANCHO, 171f)
        drawRect(Brush.verticalGradient(listOf(cara(l, r, C.HORMIGON, 0.5f * r.dia, 0.28f), cara(l, r, C.HORMIGON_OSC, frente(r), 0.28f)), tapia.top, tapia.bottom), tapia.topLeft, tapia.size)
        drawRect(l.tono(C.LUZ, 0.28f).copy(alpha = 0.5f), tapia.topLeft, Size(tapia.width, 0.8f))
        var tx = 262f; while (tx < ANCHO) { drawLine(l.tono(C.HORMIGON_OSC, 0.28f).copy(alpha = 0.6f), Offset(tx, 163.6f), Offset(tx, 171f), 0.4f); tx += az.entre(9f, 15f) }
        repeat(8) { val p = Offset(az.entre(262f, ANCHO), az.entre(164f, 170f)); drawRect(l.tono(listOf(C.HUESO, C.ROJO, C.SACO)[it % 3], 0.28f).copy(alpha = 0.7f), p, Size(az.entre(2.6f, 5f), az.entre(2.4f, 4f))) }
        if (nieve) { drawRect(l.tono(C.NIEVE, 0.28f), Offset(260f, 162.2f), Size(ANCHO - 260f, 1.4f)); for (f in listOf(::bordilloIzq, ::bordilloDer)) drawLine(l.tono(C.NIEVE, 0.1f), Offset(f(1f), BORDE), Offset(f(0.1f), gy(0.1f)), 2.2f) }
    }

    /**
     * Las vías del tranvía (va dentro del recorte de la calzada): el adoquinado entre los rieles, de otra piedra que el
     * asfalto, y los cuatro rieles. De día brillan hacia el sol, más con el sol bajo y a lo lejos; de noche devuelven la
     * luz de cada farola que tienen cerca.
     */
    private fun DrawScope.vias(m: Momento, l: Luces, r: Rayo, az: Azar, asfalto: Color) {
        val nieve = EscenaDistopia.nevado(m); val mojado = EscenaDistopia.mojado(m)
        val bajo = (1f - l.solAltura / 0.6f).coerceIn(0f, 1f)
        val brillo = (r.dia * (0.3f + 0.7f * l.solFuerza) * (0.4f + 0.6f * bajo) + (if (mojado) 0.2f * r.dia else 0f)).coerceIn(0f, 1f)
        val k = if (l.noche > 0.3f && EscenaDistopia.corriente(m)) ((l.noche - 0.3f) / 0.5f).coerceIn(0f, 1f) else 0f
        val destello = lerp(l.solColor, Color.White, 0.55f)
        for (cx in VIAS) {
            val cama = poligono(enVia(cx - 9f, FIN_DE_VIA), enVia(cx + 9f, FIN_DE_VIA), enVia(cx + 9f, 0.03f), enVia(cx - 9f, 0.03f))
            val piedra = if (nieve) lerp(C.LOSA, C.NIEVE, 0.5f) else lerp(asfalto, C.LOSA, 0.45f)
            drawPath(cama, Brush.verticalGradient(listOf(l.tono(piedra, 0.6f), l.tono(piedra, 0.3f), l.tono(piedra, 0.06f)), H, BORDE))
            clipPath(cama) {
                // Los adoquines: unos más claros, otros con brea, otros con el óxido del riel.
                repeat(60) { val u = 0.1f + 0.76f * kotlin.math.sqrt(az.real()); val X = cx + az.entre(-9f, 6f); val w = az.entre(2f, 4.6f)
                    drawPath(poligono(enVia(X, u), enVia(X + w, u), enVia(X + w, u * 0.968f), enVia(X, u * 0.968f)), l.tono(listOf(C.HOLLIN, C.HUESO, C.OXIDO)[it % 3], lejosDelAsfalto(u)).copy(alpha = az.entre(0.12f, 0.3f))) }
                // El óxido que chorrea junto a cada riel.
                for (s in listOf(-MEDIA_VIA, MEDIA_VIA)) drawPath(poligono(enVia(cx + s - 2.2f, FIN_DE_VIA), enVia(cx + s + 2.2f, FIN_DE_VIA), enVia(cx + s + 2.2f, 0.03f), enVia(cx + s - 2.2f, 0.03f)), Brush.verticalGradient(listOf(Color.Transparent, l.tono(C.OXIDO, 0.1f).copy(alpha = 0.3f)), H, BORDE))
            }
            for (s in listOf(-9f, 9f)) drawLine(l.tono(C.HOLLIN, 0.2f).copy(alpha = 0.3f), enVia(cx + s, FIN_DE_VIA), enVia(cx + s, 0.03f), 0.4f)
            for (s in listOf(-MEDIA_VIA, MEDIA_VIA)) {
                val a = enVia(cx + s, FIN_DE_VIA); val b = enVia(cx + s, 0.03f)
                // El riel: el acero oscuro de la garganta y, encima, la cabeza pulida por las ruedas.
                drawPath(poligono(enVia(cx + s - 0.9f, FIN_DE_VIA), enVia(cx + s + 0.9f, FIN_DE_VIA), enVia(cx + s + 0.9f, 0.03f), enVia(cx + s - 0.9f, 0.03f)), l.tono(lerp(C.HIERRO, C.HOLLIN, 0.5f), 0.12f))
                drawLine(l.tono(lerp(C.HIERRO, C.HOLLIN, 0.5f), 0.3f).copy(alpha = 0.8f), a, b, 0.4f)
                val cabeza = poligono(enVia(cx + s - 0.4f, FIN_DE_VIA), enVia(cx + s + 0.4f, FIN_DE_VIA), enVia(cx + s + 0.4f, 0.03f), enVia(cx + s - 0.4f, 0.03f))
                drawPath(cabeza, l.tono(Color(0xFFB8BCB8), 0.1f).copy(alpha = 0.75f))
                if (brillo > 0.02f) { drawPath(cabeza, Brush.verticalGradient(listOf(destello.copy(alpha = brillo), destello.copy(alpha = brillo * 0.45f)), H, BORDE)); drawLine(Brush.verticalGradient(listOf(destello.copy(alpha = brillo), destello.copy(alpha = brillo * 0.3f)), H, gy(0.5f)), b, enVia(cx + s, 0.5f), 0.45f) }
                // De noche, un tramo de riel encendido al pie de cada farola.
                if (k > 0f) for ((uf, der) in FAROLAS) {
                    val cerca = if ((cx > 0f) == der) 0.85f else 0.4f
                    drawLine(lerp(C.SODIO, Color.White, 0.4f).copy(alpha = cerca * k * (if (mojado) 1f else 0.8f)), enVia(cx + s, minOf(uf * 1.16f, FIN_DE_VIA)), enVia(cx + s, uf * 0.88f), 0.8f * uf + 0.3f, StrokeCap.Round, blendMode = BlendMode.Screen)
                }
            }
        }
    }

    /** El tope del final de una vía: dos pies de carril viejo, con sus jabalcones, y el travesaño pintado a franjas, con su farol rojo. */
    private fun DrawScope.tope(m: Momento, l: Luces, r: Rayo, cx: Float) {
        val u = FIN_DE_VIA; val lejos = 0.06f; val c = enVia(cx, u)
        val hierro = l.tono(C.HIERRO, lejos); val oxido = cara(l, r, C.OXIDO, lado(r, 1f), lejos)
        drawOval(Color.Black.copy(alpha = 0.26f), Offset(c.x - 9f * u, c.y - 1.4f * u), Size(18f * u, 3.4f * u))
        for (s in listOf(-MEDIA_VIA, MEDIA_VIA)) {
            val pie = enVia(cx + s, u)
            drawLine(hierro, enVia(cx + s, u * 0.93f), pie + Offset(0f, -8f * u), 1.1f * u, StrokeCap.Round)        // el jabalcón, hacia la vía
            drawLine(oxido, pie, pie + Offset(0f, -9f * u), 1.6f * u)
        }
        val viga = Rect(c.x - 8.4f * u, c.y - 10f * u, c.x + 8.4f * u, c.y - 5.6f * u)
        drawRect(Brush.verticalGradient(listOf(cara(l, r, C.HUESO, 0.6f * r.dia, lejos), cara(l, r, C.HUESO, frente(r), lejos)), viga.top, viga.bottom), viga.topLeft, viga.size)
        clipRect(viga.left, viga.top, viga.right, viga.bottom) { for ((i, x) in listOf(-8.4f, -2.6f, 3.8f).withIndex()) drawPath(poligono(Offset(c.x + x * u, viga.bottom), Offset(c.x + (x + 2.6f + 0.5f * i) * u, viga.bottom), Offset(c.x + (x + 5.4f + 0.5f * i) * u, viga.top), Offset(c.x + (x + 2.8f) * u, viga.top)), cara(l, r, C.ROJO, frente(r), lejos)) }
        drawRect(l.tono(C.HOLLIN, lejos).copy(alpha = 0.4f), Offset(viga.left, viga.bottom - 0.5f), Size(viga.width, 0.6f))
        val farol = Offset(c.x, viga.top - 1.6f * u)
        drawRect(hierro, Offset(farol.x - 1.3f * u, farol.y - 1.4f * u), Size(2.6f * u, 3f * u))
        val encendido = l.noche > 0.3f && EscenaDistopia.corriente(m)
        drawCircle(if (encendido) C.PILOTO else l.tono(C.ROJO_OSC, lejos), 0.9f * u, farol); if (encendido) resplandor(farol, 6f * u, 0.6f, C.PILOTO)
        if (EscenaDistopia.nevado(m)) drawRect(l.tono(C.NIEVE, lejos), Offset(viga.left, viga.top - 1f * u), Size(viga.width, 1.2f * u))
    }

    /**
     * La catenaria del tranvía: cada farola sostiene, con una ménsula y su tirante, el hilo de la vía de su lado; el hilo
     * cuelga un poco entre apoyo y apoyo; y al final de la línea, un vano de acera a acera del que se amarran los dos.
     */
    private fun DrawScope.catenaria(l: Luces) {
        for (cx in VIAS) {
            val der = cx > 0f
            val apoyos = FAROLAS.filter { it.second == der }.map { it.first }
            for (u in apoyos) {
                val lejos = lejosDe(u); val pie = pieDeFarola(u, der); val w = hilo(cx, u) + Offset(0f, -2.4f * u); val hierro = l.tono(C.HIERRO, lejos)
                val arranque = Offset(pie.x, pie.y - (HILO + 4f) * u)
                drawLine(hierro, arranque, w, 1.2f * u + 0.3f, StrokeCap.Round)
                drawLine(hierro, Offset(pie.x, pie.y - (HILO + 15f) * u), arranque + (w - arranque) * 0.64f, 0.4f * u + 0.2f)
                drawLine(hierro, w, hilo(cx, u), 0.4f * u + 0.2f)
                drawCircle(l.tono(C.HUESO, lejos), 0.9f * u + 0.15f, w)
            }
            val pasos = listOf(FIN_DE_VIA - 0.02f) + apoyos.filter { it < FIN_DE_VIA - 0.03f } + listOf(0.05f)
            for (i in 0 until pasos.size - 1) {
                val ua = pasos[i]; val ub = pasos[i + 1]; val a = hilo(cx, ua); val b = hilo(cx, ub); val um = (ua + ub) / 2f
                val medio = hilo(cx, um) + Offset(0f, 1.8f * um); val c = medio * 2f - (a + b) * 0.5f
                drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x + (c.x - a.x) * 0.667f, a.y + (c.y - a.y) * 0.667f, b.x + (c.x - b.x) * 0.667f, b.y + (c.y - b.y) * 0.667f, b.x, b.y) },
                    l.tono(C.HOLLIN, lejosDe(um)).copy(alpha = 0.9f), style = Stroke(0.4f + 0.6f * um, cap = StrokeCap.Round))
            }
        }
        // El vano del final de la línea, de la fachada a la farola de la plaza.
        val uf = FIN_DE_VIA - 0.02f; val tinta = l.tono(C.HOLLIN, 0.08f)
        val a = Offset(fachadaIzq(uf), gy(uf) - (HILO + 5f) * uf); val b = Offset(pieDeFarola(0.808f, true).x, gy(0.808f) - (HILO + 5f) * 0.808f)
        cable(a, b, 2.2f, tinta, 0.55f)
        for (cx in VIAS) { val w = hilo(cx, uf); drawLine(tinta, w, Offset(w.x, a.y + (b.y - a.y) * ((w.x - a.x) / (b.x - a.x)) + 1.8f), 0.4f); drawCircle(l.tono(C.HUESO, 0.08f), 0.9f, w) }
    }

    /**
     * El tranvía, parado en el final de la vía de la derecha y visto de frente: la caja verde y crema con su franja
     * roja, el parabrisas partido, el faro, el costado con sus ventanillas, que se aleja, y el pantógrafo en el hilo.
     * Vacío: sin nadie dentro. De noche lleva las luces encendidas.
     */
    private fun DrawScope.tranvia(m: Momento, l: Luces, r: Rayo) {
        val cx = VIAS[1]; val u = TRANVIA_U; val u2 = u * 0.74f; val lejos = lejosDe(u); val medio = 7.6f; val alto = 21f
        fun p(X: Float, h: Float, uu: Float) = Offset(VX + X * uu, gy(uu) - h * uu)
        val verde = lerp(C.VERDE_GRIS, Color(0xFF3E5A4A), 0.6f); val luz = l.noche > 0.3f; val vidrio = Color(0xFFFFD48A)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(p(cx - medio, 0f, u2).x - 1f, gy(u) - 2.2f * u), Size((medio * 2f + 6f) * u, 4f * u))
        // El costado que mira al eje de la avenida.
        val ladoC = lado(r, -1f)
        drawPath(poligono(p(cx - medio, 1.4f, u), p(cx - medio, 1.4f, u2), p(cx - medio, 9.5f, u2), p(cx - medio, 9.5f, u)), cara(l, r, verde, ladoC, lejos))
        drawPath(poligono(p(cx - medio, 9.5f, u), p(cx - medio, 9.5f, u2), p(cx - medio, alto, u2), p(cx - medio, alto, u)), cara(l, r, C.HUESO, ladoC, lejos))
        for (i in 0..4) { val ua = u - (u - u2) * (i + 0.2f) / 5f; val ub = u - (u - u2) * (i + 0.84f) / 5f
            drawPath(poligono(p(cx - medio, 11.6f, ua), p(cx - medio, 11.6f, ub), p(cx - medio, 17.6f, ub), p(cx - medio, 17.6f, ua)), if (luz) vidrio.copy(alpha = 0.9f) else cara(l, r, lerp(C.CRISTAL, C.HOLLIN, 0.3f), ladoC, lejos)) }
        drawLine(l.tono(C.ROJO, lejos), p(cx - medio, 9.8f, u), p(cx - medio, 9.8f, u2), 0.9f * u + 0.2f)
        drawLine(l.tono(C.HOLLIN, lejos), p(cx - medio, 1.2f, u), p(cx - medio, 1.2f, u2), 1.4f * u)
        // El frente.
        val x0 = p(cx - medio, 0f, u).x; val x1 = p(cx + medio, 0f, u).x; val yb = gy(u) - 1.4f * u; val ym = gy(u) - 9.5f * u; val yt = gy(u) - alto * u
        drawRect(Brush.verticalGradient(listOf(cara(l, r, verde, frente(r), lejos), cara(l, r, lerp(verde, C.HOLLIN, 0.3f), frente(r), lejos)), ym, yb), Offset(x0, ym), Size(x1 - x0, yb - ym))
        drawRoundRect(cara(l, r, C.HUESO, frente(r), lejos), Offset(x0, yt), Size(x1 - x0, ym - yt + 0.6f), androidx.compose.ui.geometry.CornerRadius(1.6f * u))
        drawRect(l.tono(C.ROJO, lejos), Offset(x0, ym - 0.5f * u), Size(x1 - x0, 1.1f * u))
        for (k in 0..1) { val vx = x0 + (x1 - x0) * (0.09f + 0.47f * k); drawRect(if (luz) vidrio.copy(alpha = 0.92f) else cara(l, r, lerp(C.CRISTAL, C.HOLLIN, 0.25f), frente(r), lejos), Offset(vx, yt + 2.4f * u), Size((x1 - x0) * 0.35f, 6.4f * u))
            if (!luz) drawLine(l.tono(Color.White, lejos).copy(alpha = 0.4f), Offset(vx + 0.6f, yt + 3f * u), Offset(vx + (x1 - x0) * 0.16f, yt + 3f * u), 0.4f) }
        drawRect(l.tono(C.HOLLIN, lejos), Offset(x0 - 0.5f * u, yb - 1.2f * u), Size(x1 - x0 + 1f * u, 1.6f * u))               // el parachoques
        drawRect(l.tono(C.HORMIGON_OSC, lejos), Offset(x0 + 0.6f * u, yt - 1.2f * u), Size(x1 - x0 - 1.2f * u, 1.6f * u))       // el techo
        // El faro, en medio; de noche alumbra los rieles de delante.
        val faro = Offset((x0 + x1) / 2f, ym + 3.4f * u)
        drawCircle(l.tono(C.HIERRO, lejos), 1.7f * u, faro); drawCircle(if (luz) Color(0xFFFFF4D0) else l.tono(C.HUESO, lejos), 1.2f * u, faro)
        if (luz) { resplandor(faro, 9f * u, 0.7f, Color(0xFFFFF0C0)); haloDeBruma(Offset(faro.x, gy(u) + 3f * u), 15f * u, 4f * u, Color(0xFFFFF0C0), 0.6f) }
        // El pantógrafo.
        val um = u * 0.88f; val techo = p(cx, alto + 1f, um); val arriba = hilo(cx, um); val hierro = l.tono(C.HOLLIN, lejos)
        drawLine(hierro, techo + Offset(-2.6f * u, 0f), arriba + Offset(0f, 0.4f), 0.5f * u + 0.15f); drawLine(hierro, techo + Offset(2.6f * u, 0f), arriba + Offset(0f, 0.4f), 0.5f * u + 0.15f)
        drawLine(hierro, arriba + Offset(-3.4f * u, 0.3f), arriba + Offset(3.4f * u, 0.3f), 0.6f * u + 0.15f)
        if (EscenaDistopia.nevado(m)) drawRect(l.tono(C.NIEVE, lejos), Offset(x0 + 0.6f * u, yt - 2f * u), Size(x1 - x0 - 1.2f * u, 1.2f * u))
    }

    /** Las sombras que cruzan la avenida: por la mañana, las de los bloques de la derecha; por la tarde, las de la izquierda. Y la de cada farola. */
    private fun DrawScope.sombrasDeLaAvenida(r: Rayo, c: Color) {
        val solDer = r.lat >= 0f
        clipRect(0f, H + 1f, ANCHO, BORDE + 2f) {
            for (b in BLOQUES) if ((b.lado > 0f) == solDer) {
                val h0 = b.alto * b.u0; val h1 = b.alto * b.u1
                drawPath(poligono(Offset(b.fx(b.u0), gy(b.u0)), Offset(b.fx(b.u1), gy(b.u1)), Offset(b.fx(b.u1) + r.ax * h1 * 0.34f, gy(b.u1) + r.ay * h1 * 0.08f), Offset(b.fx(b.u0) + r.ax * h0 * 0.34f, gy(b.u0) + r.ay * h0 * 0.08f)), c)
            }
            for ((u, der) in FAROLAS) { val p = pieDeFarola(u, der); val h = 147f * u; drawLine(c, p, p + Offset(r.ax * h * 0.34f, r.ay * h * 0.08f), 1.6f * u + 0.3f) }
            for (u in listOf(0.66f, 0.42f, 0.28f)) { val p = Offset(VX + 106f * u, gy(u)); drawOval(c, Offset(p.x + r.ax * 30f * u - 12f * u, p.y + r.ay * 8f * u - 2.4f * u), Size(24f * u, 4.8f * u)) }
        }
    }

    /** De noche, el charco de luz que cada farola deja en el suelo, y el de la pantalla; con lluvia, su reflejo en el asfalto mojado. */
    private fun DrawScope.charcosDeLuz(m: Momento, l: Luces) {
        if (l.noche < 0.3f || !EscenaDistopia.corriente(m)) return
        val k = ((l.noche - 0.3f) / 0.5f).coerceIn(0f, 1f)
        val az = Azar(Semilla.de("distopia", "reflejos"))
        clipRect(0f, H, ANCHO, BORDE + 1f) {
            for ((u, der) in FAROLAS) {
                val p = pieDeFarola(u, der)
                haloDeBruma(p + Offset(if (der) -6f * u else 6f * u, 1f), 64f * u, 11f * u, C.SODIO, 0.9f * k)
                if (EscenaDistopia.mojado(m)) reflejoMojado(p + Offset(if (der) -8f * u else 8f * u, 0f), 26f * u, 5f * u, C.SODIO, 0.6f * k, az, 1.2f * u + 0.3f)
            }
            haloDeBruma(Offset(EscenaDistopia.PANTALLA_C.x - 24f, 178f), 46f, 9f, C.PANTALLA, 0.5f * k)
            if (EscenaDistopia.mojado(m)) reflejoMojado(Offset(EscenaDistopia.PANTALLA_C.x - 30f, 170f), 30f, 14f, C.PANTALLA, 0.4f * k, az)
        }
    }

    // ---------------------------------------------------------------- Lo que hay en la avenida

    /** Una farola de la avenida: el fuste de hormigón, el brazo doble y las dos cabezas. Los días de desfile lleva su bandera. */
    private fun DrawScope.farola(m: Momento, l: Luces, r: Rayo, u: Float, derecha: Boolean) {
        val lejos = lejosDe(u); val pie = pieDeFarola(u, derecha); val alto = 147f * u; val tope = pie + Offset(0f, -alto)
        val g = 2.6f * u + 0.3f
        val fuste = poligono(pie + Offset(-g * 0.7f, 0f), tope + Offset(-g * 0.34f, 0f), tope + Offset(g * 0.34f, 0f), pie + Offset(g * 0.7f, 0f))
        drawPath(fuste, Brush.horizontalGradient(if (r.lat >= 0f) listOf(cara(l, r, C.HORMIGON_OSC, -0.7f, lejos), cara(l, r, C.HORMIGON, 0.8f, lejos)) else listOf(cara(l, r, C.HORMIGON, 0.8f, lejos), cara(l, r, C.HORMIGON_OSC, -0.7f, lejos)), pie.x - g, pie.x + g))
        drawRect(l.tono(C.HORMIGON_OSC, lejos), pie + Offset(-g, -5f * u), Size(g * 2f, 5f * u))
        val hierro = l.tono(C.HIERRO, lejos)
        val encendida = EscenaDistopia.farolas(m)
        for (s in listOf(-1f, 1f)) {
            val cabeza = tope + Offset(s * 9f * u, 2.2f * u)
            drawPath(Path().apply { moveTo(tope.x, tope.y + 3f * u); cubicTo(tope.x + s * 2f * u, tope.y - 2f * u, tope.x + s * 7f * u, tope.y - 1.4f * u, cabeza.x, cabeza.y) }, hierro, style = Stroke(0.9f * u + 0.25f, cap = StrokeCap.Round))
            drawOval(hierro, Offset(cabeza.x - 3.4f * u, cabeza.y - 1f * u), Size(6.8f * u, 2.2f * u))
            if (encendida) { drawOval(lerp(C.SODIO, Color.White, 0.6f), Offset(cabeza.x - 2.8f * u, cabeza.y + 0.5f * u), Size(5.6f * u, 1.4f * u + 0.3f)); resplandor(cabeza + Offset(0f, 2f * u), 15f * u + 2f, 0.7f, C.SODIO) }
            else drawOval(l.tono(C.HUESO, lejos).copy(alpha = 0.8f), Offset(cabeza.x - 2.8f * u, cabeza.y + 0.5f * u), Size(5.6f * u, 1.2f * u + 0.2f))
        }
        if (EscenaDistopia.desfile(m) && u > 0.14f) {
            // La bandera de la farola: roja, con el ojo.
            val a = tope + Offset(g * 0.5f, 14f * u); val w = 13f * u; val h = 19f * u
            val pano = poligono(a, a + Offset(w, 1f * u), a + Offset(w * 0.94f, h), a + Offset(0f, h - 1f * u))
            drawPath(pano, Brush.horizontalGradient(listOf(l.tono(C.ROJO, lejos), l.tono(lerp(C.ROJO, C.LUZ, 0.2f), lejos), l.tono(C.ROJO_OSC, lejos)), a.x, a.x + w))
            if (u > 0.3f) ojo(a + Offset(w * 0.5f, h * 0.45f), w * 0.7f, l.tono(C.HUESO, lejos), l.tono(C.ROJO_OSC, lejos))
        }
        if (EscenaDistopia.nevado(m)) drawRect(l.tono(C.NIEVE, lejos), pie + Offset(-g * 1.4f, -1f * u), Size(g * 2.8f, 1.4f * u))
    }

    /**
     * Un árbol desmochado de la avenida: el tronco, la cabeza nudosa de tantas podas y, según la estación, los brotes
     * pelados o unos manojos de hojas. Cada uno tiene su tronco y sus muñones.
     */
    private fun DrawScope.desmochado(m: Momento, l: Luces, r: Rayo, pie: Offset, alto: Float, lejos: Float, n: Int) {
        val az = Azar(Semilla.de("distopia", "arbol", n))
        val k = alto / 50f
        val corteza = l.tono(Color(0xFF7A7264), lejos); val cortezaSombra = l.tono(mult(Color(0xFF7A7264), r.color, 0.8f * r.contraste), lejos)
        val cruz = pie + Offset(az.entre(-2f, 2f) * k, -alto * 0.56f)
        // El alcorque de hormigón.
        drawOval(l.tono(C.HORMIGON_OSC, lejos), Offset(pie.x - 6f * k, pie.y - 1.4f * k), Size(12f * k, 3f * k)); drawOval(l.tono(C.HOLLIN, lejos).copy(alpha = 0.6f), Offset(pie.x - 4.4f * k, pie.y - 0.9f * k), Size(8.8f * k, 1.9f * k))
        val tronco = poligono(pie + Offset(-2.2f * k, 0f), cruz + Offset(-1.7f * k, 0f), cruz + Offset(1.7f * k, 0f), pie + Offset(2.2f * k, 0f))
        drawPath(tronco, Brush.horizontalGradient(if (r.lat >= 0f) listOf(cortezaSombra, corteza) else listOf(corteza, cortezaSombra), pie.x - 2.2f * k, pie.x + 2.2f * k))
        // Los brazos, abiertos como un candelabro, y sus muñones.
        val hoja = hojaDe(m.ficha.estacion)
        val brazos = 5 + az.entero(2)
        for (i in 0 until brazos) {
            val ang = -3.14f * (0.06f + 0.88f * i / (brazos - 1f)) + az.entre(-0.1f, 0.1f); val largo = alto * az.entre(0.26f, 0.4f) * (0.72f + 0.28f * abs(cos(ang)))
            val codo = cruz + Offset(cos(ang) * largo * 0.55f, sin(ang) * largo * 0.2f)
            val fin = cruz + Offset(cos(ang) * largo, sin(ang) * largo * 0.8f - largo * 0.12f)
            drawPath(Path().apply { moveTo(cruz.x, cruz.y); cubicTo(codo.x, codo.y, codo.x, codo.y, fin.x, fin.y) }, if (cos(ang) * r.lat > 0f) corteza else cortezaSombra, style = Stroke(1.9f * k + 0.2f, cap = StrokeCap.Round))
            drawCircle(corteza, 1.8f * k, fin)
            // Los brotes del año: varas finas hacia arriba, y en cada una sus hojas, pocas y con polvo.
            val verde = if (hoja != null) lerp(hoja, Color(0xFF7A8468), 0.45f) else Color.Transparent
            repeat(4) { v -> val a2 = -1.57f + az.entre(-0.7f, 0.7f) + 0.3f * cos(ang); val l2 = alto * az.entre(0.12f, 0.24f); val punta = fin + Offset(cos(a2) * l2, sin(a2) * l2)
                drawLine(cortezaSombra, fin, punta, 0.45f * k + 0.2f, StrokeCap.Round)
                if (hoja != null) for (t in listOf(0.45f, 0.75f, 1f)) { val q = fin + (punta - fin) * t + Offset(az.entre(-1.2f, 1.2f) * k, 0f); val rad = az.entre(1.3f, 2.3f) * k
                    drawCircle(l.tono(if ((q.x - pie.x) * r.lat > 0f) lerp(verde, C.LUZ, 0.25f) else lerp(verde, Color(0xFF2E3A2A), 0.3f), lejos), rad, q) }
                if (EscenaDistopia.nevado(m) && v == 0) drawOval(l.tono(C.NIEVE, lejos), fin + Offset(-2.2f * k, -2.8f * k), Size(4.4f * k, 1.8f * k)) }
        }
    }

    /**
     * El furgón negro de la patrulla, arrimado al bordillo de la izquierda y visto por detrás: la caja sin ventanas, las
     * dos puertas, la rejilla y los pilotos. [enMarcha]: con las luces encendidas, el día que viene a por alguien.
     */
    private fun DrawScope.furgon(m: Momento, l: Luces, r: Rayo, u: Float, enMarcha: Boolean) {
        val lejos = lejosDe(u); val x0 = bordilloIzq(u) + 4f * u; val y = gy(u); val w = 15f * u; val h = 13f * u
        val negro = Color(0xFF2E3238)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(x0 - 2f * u, y - 2f * u), Size(w + 10f * u, 4f * u))
        // El costado, que se aleja, y la trasera.
        val fuga = 0.84f; val x1 = bordilloIzq(u * fuga) + 4f * u * fuga + w * fuga; val y1 = gy(u * fuga)
        drawPath(poligono(Offset(x0 + w, y - h), Offset(x1, y1 - h * fuga), Offset(x1, y1 - 1.6f * u), Offset(x0 + w, y - 2f * u)), cara(l, r, negro, lado(r, 1f), lejos))
        drawRoundRect(Brush.verticalGradient(listOf(cara(l, r, lerp(negro, C.HIERRO, 0.5f), frente(r), lejos), cara(l, r, negro, frente(r), lejos)), y - h, y), Offset(x0, y - h), Size(w, h - 2f * u), androidx.compose.ui.geometry.CornerRadius(1.6f * u))
        drawLine(l.tono(C.HIERRO, lejos), Offset(x0 + w / 2f, y - h + 1.6f * u), Offset(x0 + w / 2f, y - 2.4f * u), 0.3f + 0.3f * u)
        for (k in listOf(0.14f, 0.58f)) { drawRect(l.tono(lerp(C.CRISTAL, C.HOLLIN, 0.5f), lejos), Offset(x0 + w * k, y - h + 2f * u), Size(w * 0.28f, 3f * u)); for (i in 1..2) drawLine(l.tono(C.HIERRO, lejos), Offset(x0 + w * (k + 0.093f * i), y - h + 2f * u), Offset(x0 + w * (k + 0.093f * i), y - h + 5f * u), 0.25f + 0.2f * u) }
        drawRect(l.tono(C.HOLLIN, lejos), Offset(x0 - 0.6f * u, y - 3.4f * u), Size(w + 1.2f * u, 1.4f * u))
        for (k in listOf(0.08f, 0.72f)) drawRoundRect(l.tono(Color(0xFF1C1A1C), lejos), Offset(x0 + w * k, y - 2.6f * u), Size(w * 0.2f, 2.8f * u), androidx.compose.ui.geometry.CornerRadius(0.8f * u))
        // Los pilotos; y, cuando viene a por alguien, los faros alumbrando la fachada.
        val encendidos = enMarcha || (l.noche > 0.3f && false)
        for (k in listOf(0.06f, 0.86f)) { val q = Offset(x0 + w * (k + 0.04f), y - 4.6f * u); drawCircle(if (enMarcha) C.PILOTO else l.tono(C.ROJO_OSC, lejos), 0.9f * u + 0.2f, q); if (encendidos) resplandor(q, 5f * u, 0.7f, C.PILOTO) }
        if (enMarcha) { haloDeBruma(Offset(x1 + 4f * u, y1 - 6f * u), 26f * u, 12f * u, Color(0xFFFFF0C0), 0.6f); haloDeBruma(Offset(x0 - 10f * u, y - 16f * u), 16f * u, 22f * u, Color(0xFFFFF0C0), 0.35f) }
    }

    /** La pantalla grande de la fachada: da las noticias a la calle, sin letras. No se apaga nunca; solo en el apagón. */
    private fun DrawScope.pantalla(m: Momento, l: Luces) {
        val c = EscenaDistopia.PANTALLA_C; val zona = Rect(c.x - 13.5f, c.y - 9f, c.x + 13.5f, c.y + 9f)
        drawRect(l.tono(C.HOLLIN, 0.2f), Offset(zona.left - 1.2f, zona.top - 1.2f), Size(zona.width + 2.4f, zona.height + 2.4f))
        if (!EscenaDistopia.corriente(m)) { drawRect(l.tono(lerp(C.CRISTAL, C.HOLLIN, 0.6f), 0.2f), zona.topLeft, zona.size); return }
        val az = m.azar("pantalla", (m.hora * 3).toInt())
        val campana = EscenaDistopia.enCampana(m) || EscenaDistopia.desfile(m)
        val fondo = if (campana) lerp(C.ROJO, C.PANTALLA, 0.25f) else lerp(C.PANTALLA, Color(0xFF2A4A4A), 0.45f)
        drawRect(Brush.verticalGradient(listOf(lerp(fondo, Color.White, 0.25f), fondo), zona.top, zona.bottom), zona.topLeft, zona.size)
        clipRect(zona.left, zona.top, zona.right, zona.bottom) {
            // Lo que da hoy: el ojo, unas barras de cifras que suben o unas franjas.
            val claro = lerp(fondo, Color.White, 0.7f)
            when (az.entero(3)) {
                0 -> ojo(c, 20f, claro, lerp(fondo, C.HOLLIN, 0.6f))
                1 -> for (i in 0..5) { val h = 3f + 2f * i + az.entre(-1f, 1f); drawRect(claro.copy(alpha = 0.9f), Offset(zona.left + 3f + i * 3.8f, zona.bottom - 2f - h), Size(2.4f, h)) }
                else -> for (i in 0..2) drawRect(claro.copy(alpha = 0.85f - 0.2f * i), Offset(zona.left + 3f, zona.top + 3f + i * 5f), Size(zona.width * (0.8f - 0.2f * i), 2.2f))
            }
            for (y in generateSequence(zona.top) { it + 1.5f }.takeWhile { it < zona.bottom }) drawLine(Color.Black.copy(alpha = 0.12f), Offset(zona.left, y), Offset(zona.right, y), 0.4f)
        }
        val k = 0.25f + 0.75f * l.noche
        drawRect(fondo.copy(alpha = 0.2f * k), Offset(zona.left - 8f, zona.top - 6f), Size(zona.width + 16f, zona.height + 14f), blendMode = BlendMode.Plus)
    }

    /** Lo que cuelga de farola a farola: banderines rojos en el desfile; ristras de bombillas en la fiesta. */
    private fun DrawScope.colgadoDeLaAvenida(m: Momento, l: Luces) {
        val tramos = listOf(0.62f to 0.5f, 0.4f to 0.33f, 0.27f to 0.22f)
        for ((ui, ud) in tramos) {
            val a = pieDeFarola(ui, false) + Offset(0f, -120f * ui); val b = pieDeFarola(ud, true) + Offset(0f, -120f * ud); val u = (ui + ud) / 2f
            val pts = (0..12).map { i -> val t = i / 12f; Offset(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t + 14f * u * 4f * t * (1f - t)) }
            drawPath(Path().apply { moveTo(pts[0].x, pts[0].y); for (p in pts.drop(1)) lineTo(p.x, p.y) }, l.tono(C.HIERRO, lejosDe(u)), style = Stroke(0.3f + 0.3f * u))
            for ((i, p) in pts.withIndex()) {
                if (i == 0 || i == 12) continue
                if (EscenaDistopia.desfile(m)) drawPath(poligono(p + Offset(-2.4f * u, 0f), p + Offset(2.4f * u, 0f), p + Offset(0f, 7f * u)), l.tono(if (i % 3 == 0) C.HUESO else C.ROJO, lejosDe(u)))
                else { val on = l.noche > 0.3f && EscenaDistopia.corriente(m); drawCircle(if (on) lerp(listOf(C.SODIO, C.PILOTO, C.HUESO)[i % 3], Color.White, 0.3f) else l.tono(listOf(C.SODIO, C.ROJO, C.HUESO)[i % 3], lejosDe(u)), 1.3f * u + 0.3f, p + Offset(0f, 1.4f * u)); if (on) resplandor(p, 6f * u + 1f, 0.5f, C.SODIO) }
            }
        }
    }

    /** Dónde está la caseta de la ración. */
    internal val CASETA = Rect(262f, 151f, 310f, 193f)

    /**
     * La caseta de la ración, al fondo de la plaza: de hormigón, con su ventanilla y su persiana. A la hora de comer
     * está abierta y humea; el resto del día, cerrada. A su lado, los sacos: cada año son menos ([EscenaDistopia.sacos]).
     */
    private fun DrawScope.caseta(m: Momento, l: Luces, r: Rayo) {
        val z = CASETA; val lejos = 0.16f
        val abierta = EscenaDistopia.comiendo(m)
        if (r.fuerza > 0.05f) drawOval(r.color, Offset(z.left - 4f + r.ax * 6f, z.bottom - 2.4f), Size(z.width + 8f, 5f), alpha = r.fuerza * 0.8f, blendMode = BlendMode.Multiply)
        val c = cara(l, r, C.HORMIGON, frente(r), lejos)
        drawRect(Brush.verticalGradient(listOf(c, lerp(c, l.tono(C.HORMIGON_OSC, lejos), 0.4f)), z.top, z.bottom), z.topLeft, z.size)
        drawRect(cara(l, r, C.HORMIGON, lado(r, -1f), lejos), z.topLeft, Size(5f, z.height)); drawRect(cara(l, r, C.HORMIGON, lado(r, 1f), lejos), Offset(z.right - 5f, z.top), Size(5f, z.height))
        // La visera y su sombra.
        drawPath(poligono(Offset(z.left - 4f, z.top), Offset(z.right + 4f, z.top), Offset(z.right + 2f, z.top - 3.4f), Offset(z.left - 2f, z.top - 3.4f)), cara(l, r, C.HORMIGON_OSC, 0.4f * r.dia, lejos))
        drawRect(r.color, Offset(z.left, z.top), Size(z.width, 2.2f), alpha = 0.6f * r.contraste, blendMode = BlendMode.Multiply)
        // La ventanilla: la persiana de lamas, a medio subir o bajada; dentro, la luz y el perol.
        val v = Rect(z.left + 9f, z.top + 9f, z.right - 9f, z.top + 26f)
        val dentro = if (abierta && l.noche > 0.2f && EscenaDistopia.corriente(m)) Color(0xFFFFD48A) else l.tono(lerp(C.HOLLIN, C.OXIDO, if (abierta) 0.3f else 0f), lejos)
        drawRect(dentro, v.topLeft, v.size)
        if (abierta) { drawOval(l.tono(C.HIERRO, lejos), Offset(v.left + 5f, v.bottom - 5.4f), Size(9f, 5.4f)); drawRect(l.tono(C.HUESO, lejos), Offset(v.left + 16f, v.bottom - 3f), Size(5f, 3f)) }
        val baja = if (abierta) v.top + 4f else v.bottom
        var y = v.top; while (y < baja) { drawRect(l.tono(if (((y - v.top) / 1.6f).toInt() % 2 == 0) C.VERDE_GRIS else lerp(C.VERDE_GRIS, C.HOLLIN, 0.3f), lejos), Offset(v.left, y), Size(v.width, minOf(1.6f, baja - y))); y += 1.6f }
        drawRect(l.tono(C.HORMIGON_OSC, lejos), Offset(v.left - 1.4f, v.bottom), Size(v.width + 2.8f, 1.6f))
        // El emblema, encima, y los restos de carteles.
        ojo(Offset(z.center.x, z.top + 4.6f), 11f, l.tono(C.ROJO, lejos), l.tono(C.ROJO_OSC, lejos), 0.85f)
        val az = Azar(Semilla.de("distopia", "caseta"))
        repeat(4) { drawRect(l.tono(listOf(C.HUESO, C.SACO)[it % 2], lejos).copy(alpha = 0.7f), Offset(az.entre(z.left + 1f, z.right - 6f), az.entre(v.bottom + 3f, z.bottom - 6f)), Size(az.entre(3f, 5f), az.entre(3f, 5f))) }
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(C.HOLLIN, lejos).copy(alpha = 0.3f)), z.bottom - 8f, z.bottom), Offset(z.left, z.bottom - 8f), Size(z.width, 8f))
        // Los sacos de la ración, apilados a la izquierda.
        val n = EscenaDistopia.sacos(m)
        for (i in 0 until n) {
            val fila = if (i < 3) 0 else if (i < 5) 1 else 2; val enFila = if (i < 3) i else if (i < 5) i - 3 else i - 5
            val p = Offset(z.left - 22f + enFila * 7.4f + fila * 3.6f, z.bottom + 2f - fila * 4.6f)
            val saco = Path().apply { moveTo(p.x, p.y); cubicTo(p.x - 1f, p.y - 4.4f, p.x + 1f, p.y - 5.4f, p.x + 3.8f, p.y - 5.2f); cubicTo(p.x + 7f, p.y - 5.6f, p.x + 8.6f, p.y - 4f, p.x + 7.6f, p.y); close() }
            drawPath(saco, Brush.horizontalGradient(if (r.lat >= 0f) listOf(l.tono(mult(C.SACO, r.color, 0.7f * r.contraste), lejos), l.tono(C.SACO, lejos)) else listOf(l.tono(C.SACO, lejos), l.tono(mult(C.SACO, r.color, 0.7f * r.contraste), lejos)), p.x, p.x + 8f))
            drawLine(l.tono(C.OXIDO, lejos).copy(alpha = 0.6f), p + Offset(1.6f, -4.8f), p + Offset(2f, -0.4f), 0.3f)
        }
        if (EscenaDistopia.nevado(m)) drawRect(l.tono(C.NIEVE, lejos), Offset(z.left - 2f, z.top - 4.4f), Size(z.width + 4f, 1.6f))
    }

    /** Las vallas de la cola, delante de la caseta: se adivina por ellas dónde se espera. Los días de desfile bordean además la avenida. */
    private fun DrawScope.vallas(m: Momento, l: Luces, r: Rayo) {
        val hierro = l.tono(C.VERDE_GRIS, 0.14f); val sombra = l.tono(mult(C.VERDE_GRIS, r.color, 0.8f), 0.14f)
        fun valla(a: Offset, b: Offset, alto: Float) {
            if (r.fuerza > 0.05f) drawLine(r.color.copy(alpha = r.fuerza * 0.5f), a + Offset(r.ax * 2f, 0.6f), b + Offset(r.ax * 2f, 0.6f), 1.4f, blendMode = BlendMode.Multiply)
            drawLine(hierro, a + Offset(0f, -alto), b + Offset(0f, -alto), 0.9f, StrokeCap.Round); drawLine(sombra, a + Offset(0f, -alto * 0.2f), b + Offset(0f, -alto * 0.2f), 0.7f)
            val n = ((b - a).getDistance() / 2.2f).toInt().coerceAtLeast(2)
            for (i in 0..n) { val p = a + (b - a) * (i / n.toFloat()); drawLine(if (i % 2 == 0) hierro else sombra, p + Offset(0f, -alto), p + Offset(0f, if (i == 0 || i == n) 0f else -alto * 0.2f), if (i == 0 || i == n) 0.9f else 0.4f) }
        }
        valla(Offset(252f, 199f), Offset(276f, 199.6f), 10f); valla(Offset(280f, 203.4f), Offset(310f, 202.6f), 11f); valla(Offset(238f, 204f), Offset(254f, 201f), 10.4f)
        // Una, volcada.
        drawLine(sombra, Offset(316f, 196f), Offset(336f, 198f), 1f, StrokeCap.Round); drawLine(hierro, Offset(317f, 193.6f), Offset(337f, 195.4f), 0.8f, StrokeCap.Round); for (i in 0..6) drawLine(hierro, Offset(318f + i * 3f, 193.8f + i * 0.26f), Offset(317.4f + i * 3f, 196.2f + i * 0.28f), 0.35f)
        if (EscenaDistopia.desfile(m)) for (f in listOf(::bordilloIzq, ::bordilloDer)) {
            var u = 0.86f
            while (u > 0.2f) { val u1 = u * 0.86f; val s = if (f(1f) < VX) -1f else 1f; valla(Offset(f(u) + s * 1f, gy(u)), Offset(f(u1) + s * 1f, gy(u1)), 9f * u); u = u1 * 0.97f }
        }
    }

    // ---------------------------------------------------------------- La parte viva

    fun DrawScope.capitalViva(m: Momento, l: Luces, reloj: Float) {
        val viento = -(0.3f + 0.7f * m.viento)
        // El humo de las chimeneas, que no para; en campaña y a las horas de trabajo, más.
        val mucho = if (EscenaDistopia.enCampana(m) || m.hacer == Hacer.TRABAJO) 1f else 0.6f
        for ((i, ch) in CHIMENEAS.withIndex()) humo(l, Offset(ch.first, ch.second - 1f), 54f, 12f, reloj, viento, 0.5f, 0.55f, 10 + i, 9, mucho)
        // El perol de la caseta, a la hora de comer.
        if (EscenaDistopia.comiendo(m)) humo(l, Offset(CASETA.left + 18f, CASETA.top + 12f), 26f, 7f, reloj, viento * 0.5f, 0.05f, 0.1f, 3, 7, 0.7f)
        // Los cohetes: columnas de humo negro en el barrio de los obreros.
        if (EscenaDistopia.cohetes(m)) for ((i, x) in listOf(296f, 318f, 354f).withIndex()) humo(l, Offset(x, H - 2f), 90f, 22f, reloj, viento, 0.95f, 0.4f, 30 + i, 10, 1.2f)
        // La pantalla parpadea.
        if (EscenaDistopia.corriente(m)) { val c = EscenaDistopia.PANTALLA_C; val late = 0.5f + 0.5f * sin(reloj * 5.3f) * sin(reloj * 1.7f); drawRect(Color.White.copy(alpha = 0.05f + 0.07f * late), Offset(c.x - 13.5f, c.y - 9f), Size(27f, 18f)); val y = c.y - 9f + ((reloj * 3f) % 18f); drawLine(Color.White.copy(alpha = 0.2f), Offset(c.x - 13.5f, y), Offset(c.x + 13.5f, y), 0.6f) }
        // Los pilotos de las chimeneas y de las grúas laten.
        if (l.noche > 0.4f && EscenaDistopia.corriente(m)) for ((i, ch) in CHIMENEAS.withIndex()) drawCircle(C.PILOTO.copy(alpha = (0.5f + 0.5f * sin(reloj * 2f + i)).coerceIn(0f, 1f) * 0.5f), 2f, Offset(ch.first, ch.second - 0.6f))
    }
}
