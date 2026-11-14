package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.vidas.Azar
import kotlin.math.min

/**
 * Una estancia vista de frente, desde dentro: una caja en perspectiva con un solo punto de fuga. Es la pieza de los
 * interiores con ventana (`docs/diseno/ESCENAS.md`, sección 5); sale de la capilla de Roma (`EscenaUrbeCapilla.kt`),
 * que fue el primero y la lleva todavía por dentro.
 *
 * La estancia se mide en unidades de la pared del fondo (una unidad es una del lienzo en esa pared): [x0] y [x1] son
 * sus paredes, [techo] y [suelo] sus alturas. La tercera medida, `t`, es la distancia a la pared del fondo hacia quien
 * mira. [p] lleva un punto de la estancia al lienzo.
 *
 * - [fuga]: los ojos de quien mira.
 * - [ojo]: a cuántas unidades está quien mira de la pared del fondo. Cuanto más pequeño, más se abren las paredes.
 * - [cerca]: hasta qué `t` se dibuja (más acá ya queda fuera del cuadro).
 */
class Estancia(val fuga: Offset, val ojo: Float, val x0: Float, val x1: Float, val techo: Float, val suelo: Float, val cerca: Float) {
    val anchura: Float get() = x1 - x0

    /** Cuánto se agranda lo que está a [t] de la pared del fondo. */
    fun s(t: Float): Float = 1f / (1f - t.coerceIn(-3f * ojo, 0.95f * ojo) / ojo)

    /** El punto del lienzo de lo que está en ([x], [y]) a [t] de la pared del fondo. */
    fun p(x: Float, y: Float, t: Float = 0f): Offset { val k = s(t); return Offset(fuga.x + (x - fuga.x) * k, fuga.y + (y - fuga.y) * k) }

    fun formaDelFondo(): Path = poligono(p(x0, techo), p(x1, techo), p(x1, suelo), p(x0, suelo))
    fun formaDelSuelo(): Path = poligono(p(x0, suelo), p(x1, suelo), p(x1, suelo, cerca), p(x0, suelo, cerca))
    fun formaDelTecho(): Path = poligono(p(x0, techo), p(x1, techo), p(x1, techo, cerca), p(x0, techo, cerca))
    fun formaIzquierda(): Path = poligono(p(x0, techo), p(x0, suelo), p(x0, suelo, cerca), p(x0, techo, cerca))
    fun formaDerecha(): Path = poligono(p(x1, techo), p(x1, suelo), p(x1, suelo, cerca), p(x1, techo, cerca))
}

/** En qué pared lateral está el hueco por el que entra el sol. */
enum class Lado { IZQUIERDA, DERECHA }

/**
 * El sol (o la luna) que entra por un hueco de una pared lateral.
 *
 * - [e]: cuánto baja el rayo por cada unidad que cruza la estancia (grande a mediodía, pequeño con el sol bajo).
 * - [a]: cuánto se va hacia quien mira por cada unidad que cruza; negativo, hacia la pared del fondo.
 * - [fuerza], de 0 a 1, y [color]. [deLuna]: la mancha es fría y no se entibia.
 */
class SolQueEntra(val e: Float, val a: Float, val fuerza: Float, val color: Color, val deLuna: Boolean = false)

/**
 * El sol (o la luna) que entra a esta hora por un hueco que da al cielo de [l], o null si no entra nada.
 *
 * **[Aproximación]**, la misma que aprobó el autor en la capilla: el hueco da al mismo cielo que pinta `Luces`. Por
 * la mañana el sol entra bajo y cruza la estancia (la mancha sube por la pared de enfrente), a mediodía cae casi a
 * plomo junto al hueco y por la tarde vuelve a tenderse, hacia quien mira. Así 7:00, 13:00 y 21:00 dan tres cuadros
 * distintos. No es el recorrido real del sol por una ventana con una orientación dada.
 */
fun solQueEntra(l: Luces, colorDeLuna: Color = Color(0xFFBFD0F0)): SolQueEntra? {
    val r = Rayo(l)
    if (r.fuerza > 0.05f) return SolQueEntra(r.tan.coerceIn(0.24f, 1.7f), 0.24f - 0.3f * r.lat, (r.fuerza / 0.72f).coerceIn(0f, 1f), lerp(l.solColor, Color.White, 0.2f))
    if (l.noche > 0.7f && l.nublado < 0.5f && l.lunaAltura > 0.05f) return SolQueEntra(0.95f, 0.28f, 0.6f * (1f - l.nublado), colorDeLuna, deLuna = true)
    return null
}

/**
 * Dónde cae el rayo que entra por el punto del hueco que está a [t] de hondo y a [y] de alto: en el suelo, en la
 * pared de enfrente o, si va hacia el fondo, en la pared del fondo; lo que encuentre antes.
 */
fun Estancia.cae(t: Float, y: Float, sol: SolQueEntra, lado: Lado = Lado.IZQUIERDA): Offset {
    val alSuelo = (suelo - y) / sol.e; val alFondo = if (sol.a < -0.02f) t / -sol.a else Float.MAX_VALUE
    val dx = min(min(alSuelo, anchura), alFondo)
    return p(if (lado == Lado.IZQUIERDA) x0 + dx else x1 - dx, y + sol.e * dx, (t + sol.a * dx).coerceAtLeast(0f))
}

/**
 * La mancha de sol que entra por un hueco de la pared [lado]: cada uno de los [cristales] (sus esquinas, como
 * `hondo to alto`), llevado por el rayo hasta el suelo, la pared de enfrente y la pared del fondo, y recortado en cada
 * una. El borde es blando. [enParedes] rebaja la mancha en las paredes respecto del suelo (una pared oscura o pintada
 * la enseña menos).
 *
 * **[Aproximación]**: lo que hay en medio (una mesa, un atril) no corta la mancha. Quien quiera que la corte pinta
 * encima la sombra de esa cosa.
 */
fun DrawScope.manchaDeSol(e: Estancia, cristales: List<List<Pair<Float, Float>>>, sol: SolQueEntra, lado: Lado = Lado.IZQUIERDA, enParedes: Float = 0.62f) {
    val enSuelo = Path(); val enfrente = Path(); val enFondo = Path()
    fun x(dx: Float) = if (lado == Lado.IZQUIERDA) e.x0 + dx else e.x1 - dx
    for (c in cristales) {
        enSuelo.addPath(poligono(c.map { (t, y) -> val dx = (e.suelo - y) / sol.e; e.p(x(dx), e.suelo, t + sol.a * dx) }))
        enfrente.addPath(poligono(c.map { (t, y) -> e.p(x(e.anchura), y + sol.e * e.anchura, t + sol.a * e.anchura) }))
        if (sol.a < -0.02f) enFondo.addPath(poligono(c.map { (t, y) -> val dx = t / -sol.a; Offset(x(dx), y + sol.e * dx) }))
    }
    val color = colorDeLaMancha(sol)
    val deEnfrente = if (lado == Lado.IZQUIERDA) e.formaDerecha() else e.formaIzquierda()
    for ((donde, que) in listOf(e.formaDelSuelo() to enSuelo, deEnfrente to enfrente, e.formaDelFondo() to enFondo)) clipPath(donde) {
        val k = sol.fuerza * (if (que === enSuelo) 1f else enParedes)
        // La penumbra: tres trazos, cada uno más ancho y más tenue.
        for (ancho in listOf(8f, 5f, 2.4f)) drawPath(que, color.copy(alpha = 0.04f * k), style = Stroke(ancho), blendMode = BlendMode.Plus)
        drawPath(que, color.copy(alpha = 0.3f * k), blendMode = BlendMode.Plus)
    }
}

/**
 * El haz de sol en el aire, de la [boca] del hueco (su contorno, como `hondo to alto`) hasta donde cae, con [motas] de
 * polvo flotando en él. [az] decide dónde está cada mota: con semilla fija, no se mueven de un dibujo a otro.
 */
fun DrawScope.hazDeSol(e: Estancia, boca: List<Pair<Float, Float>>, sol: SolQueEntra, az: Azar, lado: Lado = Lado.IZQUIERDA, motas: Int = 44) {
    if (boca.size < 3) return
    val color = colorDeLaMancha(sol); val pared = if (lado == Lado.IZQUIERDA) e.x0 else e.x1
    for (i in boca.indices) {
        val (t0, y0) = boca[i]; val (t1, y1) = boca[(i + 1) % boca.size]
        drawPath(poligono(e.p(pared, y0, t0), e.p(pared, y1, t1), e.cae(t1, y1, sol, lado), e.cae(t0, y0, sol, lado)), color.copy(alpha = 0.03f * sol.fuerza), blendMode = BlendMode.Plus)
    }
    val t0 = boca.minOf { it.first }; val t1 = boca.maxOf { it.first }; val y0 = boca.minOf { it.second }; val y1 = boca.maxOf { it.second }
    repeat(motas) {
        val t = az.entre(t0, t1); val y = az.entre(y0, y1); val k = az.entre(0.08f, 0.96f); val a = e.p(pared, y, t); val b = e.cae(t, y, sol, lado)
        drawCircle(color.copy(alpha = (0.5f * sol.fuerza * az.real()).coerceIn(0f, 1f)), az.entre(0.3f, 0.7f), a + (b - a) * k)
    }
}

/** De sol, cálida; de luna, fría. */
private fun colorDeLaMancha(sol: SolQueEntra): Color = if (sol.deLuna) sol.color else lerp(sol.color, Color(0xFFFFD49A), 0.4f)
